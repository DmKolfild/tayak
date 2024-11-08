import re

class Analyzer:
    def __init__(self, filename):
        self.stack = "_E"
        self.transitions = []
        self.state = "S0"
        self.config = ""
        self.stackMaxLength = 10
        self.sourceFilename = filename
        self.readRules()

    def readRules(self):
        try:
            with open(self.sourceFilename, 'r') as file:
                for line in file:
                    if line[0].isalpha() and line[1] == '>':
                        left_side = line[0]
                        right_side = line[2:].strip()
                        index = self.find(self.transitions, left_side)
                        if index == -1:
                            new_rule = [left_side]
                            self.pushRules(new_rule, right_side)
                            self.transitions.append(new_rule)
                        else:
                            self.pushRules(self.transitions[index], right_side)
                    else:
                        print(f"Line #{line} contains mistakes.")
        except FileNotFoundError:
            print(f"failed to open {self.sourceFilename}")


    def find(self, transitions, terminal):
        for i in range(len(transitions)):
            if transitions[i][0] == terminal:
                return i
        return -1

    def pushRules(self, temp, right):
        matches = re.findall(r'[^|]+', right)
        temp.extend(matches)

    def isTerminal(self, seq):
        if re.match(r'[A-Z]|[A-Z][a-zA-Z]+', seq):
            return False
        else:
            return True

    def recognize_string(self, input):
        self.config = ""

        def research(input_suffix, stack, config, transitions, search_wide):
            while len(input_suffix) != 0:
                if input_suffix[0] == stack[-1]:
                    input_suffix = input_suffix[1:]
                    stack = stack[:-1]
                    if len(stack) == 1 and len(input_suffix) == 0:
                        return config + " ├ (S0, eps, h0) _"
                    elif len(stack) == 1 and len(input_suffix) != 0:
                        return "Нет конфигураций"
                    elif len(stack) != 1 and len(input_suffix) == 0:
                        return "Нет конфигураций"
                else:
                    back = stack[-1]
                    if self.isTerminal(back):
                        return "Нет конфигураций"
                    else:
                        iter = self.find(transitions, back)
                        for i in range(1, len(transitions[iter])):
                            if len(stack) > self.stackMaxLength:
                                return "N"
                            stack_copy = stack
                            reverse = transitions[iter][i][::-1]
                            stack_copy = stack_copy[:-1] + reverse
                            result = search_wide(input_suffix, stack_copy, " ├ (S0, " + input_suffix + "," + stack_copy + ")", transitions, search_wide)
                            if result[-1] == '_':
                                return config + result
                        return "Нет конфигураций"

        self.config += research(input, self.stack, "(" + f'{self.state}' + "," + input + ", _E)", self.transitions, research)

        if self.config[-1] == '_':
            return True
        else:
            return False

    def printRules(self):
        for transition in self.transitions:
            print(transition[0], end=" > ")
            for i in range(1, len(transition) - 1):
                print(transition[i], end="|")
            print(transition[len(transition) - 1])

    def printConfigs(self):
        print(self.config[:-1].replace('_', ''))

def main():
    while True:
        filename = input("Введите название файла с грамматикой: ")
        input_str = input("Введите цепочку символов: ")

        analyzer = Analyzer(filename)
        analyzer.printRules()
        if analyzer.recognize_string(input_str):
            print("Цепочка символов допустима автоматом")
        else:
            print("Цепочка символов не допустима автоматом")
        analyzer.printConfigs()

if __name__ == "__main__":
    main()
