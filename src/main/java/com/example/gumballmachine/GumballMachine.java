package com.example.gumballmachine ;

public class GumballMachine {
 
	State soldOutState;
	State noQuarterState;
	State hasQuarterState;
	State soldState;
	State errorState;

	State state = noQuarterState ;
 
	public GumballMachine() {
		soldOutState = new SoldOutState(this);
		noQuarterState = new NoQuarterState(this);
		hasQuarterState = new HasQuarterState(this);
		soldState = new SoldState(this);
		errorState = new ErrorState(this);
		state = noQuarterState ;
	}
 
	public void insertQuarter() {
		state.insertQuarter();
	}
 
	public void ejectQuarter() {
		state.ejectQuarter();
	}
 
	public void turnCrank() {
		state.turnCrank();
		state.dispense();
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setState(String state) {
		if (state.equals("com.example.gumballmachine.SoldOutState"))
		{
			this.state = this.soldOutState;
		}
		else if( state.equals("com.example.gumballmachine.NoQuarterState"))
		{
			this.state = this.noQuarterState;
		}
		else if (state.equals("com.example.gumballmachine.HasQuarterState"))
		{
			this.state = this.hasQuarterState;
		}
		else if (state.equals("com.example.gumballmachine.SoldState"))
		{
			this.state = this.soldState;
		}
		else if (state.equals("com.example.gumballmachine.ErrorState"))
		{
			this.state = this.errorState;
		}
	}
 
	void releaseBall() {
		System.out.println("A gumball comes rolling out the slot...");
	}
 
	void refill(int count) {
		state = noQuarterState;
	}

    public State getState() {
        return state;
    }

    public State getErrorState() {
    	return errorState;
    }

    public State getSoldOutState() {
        return soldOutState;
    }

    public State getNoQuarterState() {
        return noQuarterState;
    }

    public State getHasQuarterState() {
        return hasQuarterState;
    }

    public State getSoldState() {
        return soldState;
    }
 
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Mighty Gumball, Inc.");
		result.append("\nSpring Boot Standing Gumball Model #2021");
		result.append("\n\n");
		result.append("\nMachine is " + state + "\n");
		return result.toString();
	}
}
