package com.example.springgumballv3;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import com.example.gumballmachine.GumballMachine ;
import com.example.gumballmachine.HasQuarterState ;
import com.example.gumballmachine.SoldOutState ;

import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64.Encoder;

import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/")
public class GumballMachineController {

    @Autowired 
    private GumballModelRepository gumballModelRepository;

    private GumballModel g;

    private String modelNumber = "SB102927";
    private String serialNumber = "2134998871109";

    private String key = "kwRg54x2Go9iEdl49jFENRM12Mp711QI" ;
    private java.util.Base64.Encoder encoder = java.util.Base64.getEncoder() ;

    @GetMapping
    public String getAction( @ModelAttribute("command") GumballCommand command, 
                            Model model, HttpSession session) {
      
        if(g == null) {
            g = new GumballModel() ;
            g.setModelNumber( modelNumber) ;
            g.setSerialNumber( serialNumber) ;

            // Check if gumball model is already in the database
            Optional<GumballModel> gOptional = gumballModelRepository.findBySerialNumber(serialNumber);
            if(!gOptional.isPresent())
            {
                // Save new gumball model
                g.setCountGumballs(10);
                gumballModelRepository.save(g);
            }
            else
            {
                GumballModel g = gOptional.get();
                Integer currentGumballs = g.getCountGumballs();
                g.setCountGumballs(currentGumballs);
            }
        }


        model.addAttribute( "gumball", g ) ;
        
        GumballMachine gm = new GumballMachine() ;
        String message = gm.toString() + "\nModel Number: " + modelNumber + "\nSerial Number: " + serialNumber;
        session.setAttribute( "gumball", gm) ;
        // String session_id = session.getId() ;
        long timestampLong = java.lang.System.currentTimeMillis() ;
        String timestamp = String.valueOf(timestampLong) ;
        command.setTimestamp( timestamp );

        String state = gm.getState().getClass().getName() ;
        command.setState( state ) ;

        String toHash = timestamp + '/' + state ;
        byte[] hashByte = hmac_sha256( key, toHash );
        String hash = encoder.encodeToString(hashByte);
        command.setHash( hash );

        String server_ip = "" ;
        String host_name = "" ;
        try { 
            InetAddress ip = InetAddress.getLocalHost() ;
            server_ip = ip.getHostAddress() ;
            host_name = ip.getHostName() ;
  
        } catch (Exception e) { }
  
        // model.addAttribute( "session", session_id ) ;
        model.addAttribute( "hash", hash ) ;
        model.addAttribute( "message", message ) ;
        model.addAttribute( "server",  host_name + "/" + server_ip ) ;

        return "gumball" ;

    }

    @PostMapping
    public String postAction(@Valid @ModelAttribute("command") GumballCommand command,  
                            @RequestParam(value="action", required=true) String action,
                            Errors errors, Model model, HttpServletRequest request) {
    
        log.info( "Action: " + action ) ;
        log.info( "Command: " + command ) ;
    
        // HttpSession session = request.getSession() ;
        // GumballMachine gm = (GumballMachine) session.getAttribute("gumball") ;


        // Check hash integrity

        String inputState = command.getState();
        String inputTimestamp = command.getTimestamp();
        String inputHash = command.getHash();

        GumballMachine gm = new GumballMachine();
        gm.setState( inputState );

        String inputHashString = inputTimestamp + '/' + inputState;
        byte[] calcHashByte = hmac_sha256( key, inputHashString );
        String calcHash = encoder.encodeToString(calcHashByte);

        // If the hash isn't equal, then the user may have changed any of the 3 inputs
        if ( !inputHash.equals(calcHash) )
        {
            gm.setState(gm.getErrorState());
            return "gumball";
        } 

        // Check for replay attack

        long inputTimestampLong = Long.parseLong(inputTimestamp);
        long currentTimestampLong = java.lang.System.currentTimeMillis();
        long timeDifference = currentTimestampLong - inputTimestampLong;

        // Allow 30 minutes delay
        if ( timeDifference > 1800000 )
        {
            gm.setState(gm.getErrorState());
            return "gumball";
        }



        // Request is validated and action will be performed

        if ( action.equals("Insert Quarter") ) {
            gm.insertQuarter() ;
        }

        if ( action.equals("Turn Crank") ) {
            command.setMessage("") ;

            // Update database to have one less gumball
            System.out.println(gumballModelRepository.findAll() + " serial number is = " + serialNumber);
            Optional<GumballModel> gOptional = gumballModelRepository.findBySerialNumber(serialNumber);
            if(gOptional.isPresent() 
                && gm.getState() instanceof HasQuarterState 
                && !(gm.getState() instanceof SoldOutState)
                )
            {
                GumballModel g = gOptional.get();
                System.out.println("Got the gumball model = " + g);
                Integer currentGumballs = g.getCountGumballs();
                if(currentGumballs > 0)
                {
                    g.setCountGumballs(currentGumballs - 1);
                    gumballModelRepository.save(g);
                }
                else 
                {
                    gm.setState(gm.getSoldOutState());
                }
            }
            gm.turnCrank() ;
        } 

        // session.setAttribute( "gumball", gm) ;
        String message = gm.toString()  + "\nModel Number: " + modelNumber + "\nSerial Number: " + serialNumber;
        // String session_id = session.getId() ;  


        // Add GumballCommand variables

        long timestampLong = java.lang.System.currentTimeMillis() ;
        String timestamp = String.valueOf(timestampLong) ;
        command.setTimestamp( timestamp );

        String state = gm.getState().getClass().getName() ;
        command.setState( state ) ;

        String toHash = timestamp + '/' + state ;
        byte[] hashByte = hmac_sha256( key, toHash );
        String hash = encoder.encodeToString(hashByte);
        command.setHash( hash );



        String server_ip = "" ;
        String host_name = "" ;
        try { 
            InetAddress ip = InetAddress.getLocalHost() ;
            server_ip = ip.getHostAddress() ;
            host_name = ip.getHostName() ;
  
        } catch (Exception e) { }
  
        model.addAttribute( "hash", hash );
        // model.addAttribute( "session", session_id ) ;
        model.addAttribute( "message", message ) ;
        model.addAttribute( "server",  host_name + "/" + server_ip ) ;
     

        if (errors.hasErrors()) {
            return "gumball";
        }

        return "gumball";
    }

    private static byte[] hmac_sha256(String secretKey, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256") ;
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256") ;
            mac.init(secretKeySpec) ;
            byte[] digest = mac.doFinal(data.getBytes()) ;
            return digest ;
        } catch (InvalidKeyException e1) {
            throw new RuntimeException("Invalid key exception while converting to HMAC SHA256") ;
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException("Java Exception Initializing HMAC Crypto Algorithm") ;
        }
    }

}