package org.example;

import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    //Метод возвращает true, если проверяемый символ - оператор
    static private boolean IsOperator(char c) {
        String operators = "+-/*()^";
        return operators.indexOf(c) != -1;
    }

    //Метод возвращает true, если проверяемый символ - разделитель ("пробел")
    static private boolean IsDelimeter(char c) {
        return c == ' ' || c == '\t';
    }

    //Метод возвращает приоритет оператора
    static private byte GetPriority(char c) {
        return switch (c) {
            case '(', ')' -> 1;
            case '+', '-' -> 2;
            case '*', '/' -> 3;
            default -> 4;
        };
    }

    static private String GetExpression(String input) {
        Pattern pattern = Pattern.compile("pow\\((?<first>.*?),(?<second>.*?)\\)"); // регулярка для поиска pow(a,b)
        Matcher matcher = pattern.matcher(input);
        // замена всех pow(a,b) на ((a)^(b))
        while (matcher.find()) {
            input = input.replaceAll("pow\\((?<first>.*?),(?<second>.*?)\\)", "((${first})^(${second}))");
            matcher = pattern.matcher(input);
        }

        pattern = Pattern.compile("(?<=\\d)\\("); // регулярка для поиска выражений вида 5(2+2), т.е где упущен знак умножить
        matcher = pattern.matcher(input);
        // замена всех '(' на '*('
        while (matcher.find()) {
            input = input.replaceAll("(?<=\\d)\\(", "*(");
            matcher = pattern.matcher(input);
        }

        input = input.replace(",", ".");

        if (input.contains(".."))
            return "Некорректный формат чисел!";

//        pattern = Pattern.compile("\\s*[+\\-*/^]\\s*[+\\-*/^]\\s*");  // операции следуют друг за другом
//        matcher = pattern.matcher(input);
//        if (matcher.find())
//            return "Операции следуют друг за другом!";

        pattern = Pattern.compile("\\d+(\\s+\\d+)*"); // пробелы между чисел
        matcher = pattern.matcher(input);
        if (!matcher.find())
            return "Между числами не должны быть исключительно пробелы!";

        pattern = Pattern.compile("\\d+[+\\-*/^](\\s*\\d+)+"); // операция без ваторого аргумента
        matcher = pattern.matcher(input);
        if (!matcher.find())
            return "После операций должно быть число!";

        StringBuilder output = new StringBuilder(); //Строка для хранения выражения
        Stack<Character> operStack = new Stack<>(); //Стек для хранения операторов
        for (int i = 0; i < input.length(); i++) { //Для каждого символа в входной строке
            //Разделители пропускаем
            if (IsDelimeter(input.charAt(i)))
                continue; //Переходим к следующему символу
                //Если символ - цифра, то считываем все число
            else if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.') { //Если цифра
                StringBuilder tempOut = new StringBuilder();
                //Читаем до разделителя или оператора, что бы получить число
                while ((!IsDelimeter(input.charAt(i))) && !IsOperator(input.charAt(i))) {
                    if (!Character.isDigit(input.charAt(i)) && input.charAt(i) != '.')
                        return "Ошибка! " + (i + 1) + "-ый символ не является символом операции или цифрой";
                    tempOut.append(input.charAt(i)); //Добавляем каждую цифру числа к нашей строке
                    i++; //Переходим к следующему символу
                    if (i == input.length()) break; //Если символ - последний, то выходим из цикла
                }
                output.append(tempOut).append(" "); //Дописываем после числа пробел в строку с выражением
                i--; //Возвращаемся на один символ назад, к символу перед разделителем
            }

            //Если символ - оператор
            else if (IsOperator(input.charAt(i))) { //Если оператор
                if (i + 1 < input.length())
                    if (input.charAt(i) == '(' && input.charAt(i + 1) == '-') //Если символ - открывающая скобка и "-" после нее
                        output.append("0 "); //Записываем 0

                if (input.charAt(i) == '(') //Если символ - открывающая скобка
                    operStack.push(input.charAt(i)); //Записываем её в стек

                else if (input.charAt(i) == ')') //Если символ - закрывающая скобка
                {
                    //Выписываем все операторы до открывающей скобки в строку
                    try {
                        char s = operStack.pop();

                        while (s != '(') {
                            output.append(s).append(" ");
                            s = operStack.pop();
                        }
                    } catch (Exception e) {
                        return "Ошибка! Неправильно расставлены скобки!";
                    }
                } else //Если любой другой оператор
                {
                    if (!operStack.isEmpty()) //Если в стеке есть элементы
                        if (GetPriority(input.charAt(i)) <= GetPriority(operStack.peek())) //И если приоритет нашего оператора меньше или равен приоритету оператора на вершине стека
                            output.append(operStack.pop()).append(" "); //То добавляем последний оператор из стека в строку с выражением

                    operStack.push(input.charAt(i)); //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека
                }
            } else return "Ошибка! " + (i + 1) + "-ый символ не является символом операции или цифрой";
        }
        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку

        while (!operStack.isEmpty())
            output.append(operStack.pop()).append(" ");

        return output.toString(); //Возвращаем выражение в постфиксной записи
    }


    //Метод Calculate принимает выражение в виде строки и возвращает результат, в своей работе использует другие методы класса
    public static double Calculate(String input) {
        String output = GetExpression(input); //Преобразовываем выражение в постфиксную запись
        System.out.println("Преобразованное выражение: " + output.replace("^", "pow"));
        return Counting(output); //Возвращаем результат
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Введите выражение: "); //Предлагаем ввести выражение
            String parsestr = scanner.nextLine();
            if (parsestr.isEmpty()) break;

            System.out.println("Результат: " + Calculate(parsestr)); //Считываем, и выводим результат
        }
    }


    //Метод, вычисляющий значение выражения, уже преобразованного в постфиксную запись
    private static double Counting(String input) {
        double result = Double.NaN; //Результат
        Stack<Double> temp = new Stack<Double>(); //Временный стек для решения

        for (int i = 0; i < input.length(); i++) { //Для каждого символа в строке
            //Если символ - цифра, то читаем все число и записываем на вершину стека
            if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.') {
                StringBuilder a = new StringBuilder();

                while (!IsDelimeter(input.charAt(i)) && !IsOperator(input.charAt(i))) { //Пока не разделитель
                    a.append(input.charAt(i)); //Добавляем
                    i++;
                    if (i == input.length()) break;
                }
                a = new StringBuilder(a.toString().replaceAll("(?<!\\d)\\.", "0."));
                temp.push(Double.parseDouble(String.valueOf(a))); //Записываем в стек

                i--;
            }
            else if (IsOperator(input.charAt(i))) { //Если символ - оператор
                //Берем два последних значения из стека
                try {
                    double a = temp.pop();
                    double b;
                    if ((input.charAt(i) == '-' || input.charAt(i) == '+') && temp.isEmpty())
                        b = 0;
                    else
                        b = temp.pop();

                    switch (input.charAt(i)) { //И производим над ними действие, согласно оператору
                        case '+':
                            result = b + a;
                            break;
                        case '-':
                            result = b - a;
                            break;
                        case '*':
                            result = b * a;
                            break;
                        case '^':
                            if (b < 0) {
                                System.out.println("Нельзя возводить отрицательное число в степень!");
                                return Double.NaN;
                            }
                            result = Math.pow(b, a);
                            break;
                        case '/':
                            if (a == 0) {
                                System.out.println("Деление на ноль!");
                                return Double.NaN;
                            }
                            result = b / a;
                            break;
                    }
                    temp.push(result); //Результат вычисления записываем обратно в стек
                }
                catch (EmptyStackException e) {
                    System.out.println("Неверное количество служебных символов!");
                    return Double.NaN;
                }
            }
            else if (input.charAt(i) != ' ')
                return Double.NaN;
        }
        try {
            return temp.peek(); //Забираем результат всех вычислений из стека и возвращаем его
        }
        catch (EmptyStackException e) {
            System.out.println("Отсутствуют арифметичесие операции");
            return Double.NaN;
        }
    }
}
