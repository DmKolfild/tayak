package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Automate {
    public List<String> States = new ArrayList<>();
    public List<Character> Alphabet;
    public HashSet<String> FinallyStates = new HashSet<>();
    public String StartState = "q0";
    List<TransitionFunction> TransitionFunctions;


    public Automate(List<String> fileLines, List<Character> alphabet) {
        TransitionFunctions = new ArrayList<>();

        Alphabet = alphabet;
        for (String fileLine : fileLines) {
            ExecuteDescriptionLine(fileLine);
        }
        //States = States.stream().distinct().toList();
        CheckFinallyStates();
    }

    private void AddTransitionFunc(String curState, char symbol, String nextState) {
        TransitionFunctions.add(new TransitionFunction(curState, symbol, nextState));
    }

    private void ExecuteDescriptionLine(String line) {
        int i = 0;
        String curState, nextState;
        char symbol;
        String[] dataFromReadState;

        dataFromReadState = ReadState(line, i);
        curState = dataFromReadState[0];
        i = Integer.parseInt(dataFromReadState[1]);
        States.add(curState);

        i += 1;
        symbol = line.charAt(i);

        i += 2;

        dataFromReadState = ReadState(line, i);
        nextState = dataFromReadState[0];
        States.add(nextState);
        AddTransitionFunc(curState, symbol, nextState);
    }

    public void PrintTransitionFunctions() {
        for (TransitionFunction func : TransitionFunctions) {
            System.out.println(func.CurrentState + " : " + func.Symbol + " - " + func.NextState);
        }
        System.out.println();
    }

    public boolean IsAutomateDeterministic() {
        int count = 0;
        for (int i = 0; i < TransitionFunctions.size(); i++) {
            for (TransitionFunction transitionFunction : TransitionFunctions) {
                if (transitionFunction.CurrentState.equals(TransitionFunctions.get(i).CurrentState) &&
                        transitionFunction.Symbol == TransitionFunctions.get(i).Symbol) {
                    count++;
                    if (count > 1) {
                        return false;
                    }
                }
            }
            count = 0;
        }
        return true;
    }

    public boolean isExecutableForInputLine(String inputLine) {
        String curState = StartState;

        for (char symbol : inputLine.toCharArray()) {
            boolean foundTransition = false;

            for (TransitionFunction tf : TransitionFunctions) {
                if (tf.getCurrentState().equals(curState) && tf.getSymbol() == symbol) {
                    curState = tf.getNextState();
                    foundTransition = true;
                    break;
                }
            }

            if (!foundTransition) {
                return false;
            }
        }

        boolean isFinallyState = false;

        for (String fs : FinallyStates) {
            if (fs.equals(curState)) {
                isFinallyState = true;
                break;
            }
        }

        return isFinallyState;
    }

    public void determization() {

        while (!IsAutomateDeterministic()) {
            List<List<String>> newStatesByPair = new ArrayList<>();
            List<String> newStatesNames = new ArrayList<>();
            for (int i = 0; i < TransitionFunctions.size(); i++) {
                TransitionFunction func = TransitionFunctions.get(i);

                List<TransitionFunction> group = new ArrayList<>();
                for (int q = i; q < TransitionFunctions.size(); q++) {
                    if (TransitionFunctions.get(q).getCurrentState().equals(func.getCurrentState()) && TransitionFunctions.get(q).getSymbol() == func.getSymbol())
                        group.add(TransitionFunctions.get(q));
                }

                if (group.size() > 1) {
                    StringBuilder newStateName = new StringBuilder();
                    List<String> backupStates = new ArrayList<>();

                    TransitionFunction[] sortedNextStatsArray = group.stream()
                            .sorted(Comparator.comparing(TransitionFunction::getNextState))
                            .toArray(TransitionFunction[]::new);

                    for (TransitionFunction dest : sortedNextStatsArray) {
                        newStateName.append(dest.getNextState());
                        backupStates.add(dest.getNextState());
                    }

                    newStatesByPair.add(backupStates);
                    newStatesNames.add(newStateName.toString());

                    TransitionFunctions.removeIf(tf -> tf.getCurrentState().equals(group.get(0).getCurrentState()) && tf.getSymbol() == group.get(0).getSymbol());
                    TransitionFunctions.add(new TransitionFunction(group.get(0).getCurrentState(), group.get(0).getSymbol(), newStateName.toString()));
                    States.add(newStateName.toString());
                }
            }


            List<TransitionFunction> newTransF = new ArrayList<>();
            for (int i = 0; i < newStatesByPair.size(); i++) {
                List<String> newStatesList = newStatesByPair.get(i);
                for (String newStates : newStatesList) {
                    for (TransitionFunction f : TransitionFunctions) {
                        if (newStates.equals(f.getCurrentState())) {
                            newTransF.add(new TransitionFunction(newStatesNames.get(i), f.getSymbol(), f.getNextState()));
                        }
                    }
                }
            }

            TransitionFunctions.addAll(newTransF);

            TransitionFunctions = DeleteRepeatFunctions();

        }
        CheckFinallyStates();

        RemoveUnnesaseryFunctions();

        System.out.println("Automate is Determizated!\n");
    }

    private void RemoveUnnesaseryFunctions() {
        List<TransitionFunction> group = new ArrayList<>();
        for (int i = 0; i < TransitionFunctions.size(); i++) {
            int count = 0;
            TransitionFunction func = TransitionFunctions.get(i);
            for (int q = 0; q < TransitionFunctions.size(); q++) {
                if (func.getCurrentState().equals(TransitionFunctions.get(q).getNextState())) {
                    if (count == 1)
                        continue;
                    if (q != i) {
                        group.add(func);
                        count++;
                    }
                }

            }
        }
        for (TransitionFunction func : TransitionFunctions) {
            if (func.getCurrentState().equals(StartState))
                group.add(func);
        }
        TransitionFunctions = group;
    }


    private List<TransitionFunction> DeleteRepeatFunctions() {
        List<TransitionFunction> transitions = new ArrayList<>();

        for (TransitionFunction item : TransitionFunctions) {
            boolean isEntry = false;

            for (TransitionFunction item2 : transitions) {
                if (item.equals(item2)) {
                    isEntry = true;
                    break;
                }
            }

            if (!isEntry) {
                transitions.add(item);
            }
        }

        return transitions;
    }

    private void CheckFinallyStates() {
        for (String state : States) {
            if (state.contains("f")) {
                FinallyStates.add(state);
            }
        }
    }

    public String[] ReadState(String analyzingLine, int index) {
        StringBuilder state = new StringBuilder();
        while (index < analyzingLine.length() && (Character.isDigit(analyzingLine.charAt(index)) || Character.isLetter(analyzingLine.charAt(index)))) {
            state.append(analyzingLine.charAt(index));
            index++;
        }
        String[] result = new String[2];
        result[0] = state.toString();
        result[1] = String.valueOf(index);
        return result;
    }

}
