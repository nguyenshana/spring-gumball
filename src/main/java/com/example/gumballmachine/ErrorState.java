package com.example.gumballmachine ;



public class ErrorState implements State {
	GumballMachine gumballMachine;
 
	public ErrorState(GumballMachine gumballMachine) {
		this.gumballMachine = gumballMachine;
	}
  
	public void insertQuarter() {
		System.out.println("Session error. Please refresh the page.");
	}
 
	public void ejectQuarter() {
		System.out.println("Session error. Please refresh the page.");
	}
 
	public void turnCrank() {
		System.out.println("Session error. Please refresh the page.");
	}

    public void dispense() {
        System.out.println("Session error. Please refresh the page.");
    }
 
	public String toString() {
		return "in an error state. Please refresh the page";
	}
}