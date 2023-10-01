package org.example;

public class TransitionFunction {
    public String CurrentState;
    public char Symbol;
    public String NextState;

    public TransitionFunction(String curState, char symbol, String nextState) {
        CurrentState = curState;
        Symbol = symbol;
        NextState = nextState;
    }

    public boolean equals(TransitionFunction transitionFunction2) {
        return CurrentState.equals(transitionFunction2.CurrentState) &&
                Symbol == transitionFunction2.Symbol &&
                NextState.equals(transitionFunction2.NextState);
    }

    public String getCurrentState() {
        return CurrentState;
    }

    public char getSymbol() {
        return Symbol;
    }

    public String getNextState() {
        return NextState;
    }
}