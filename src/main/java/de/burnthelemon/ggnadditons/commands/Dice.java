package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.util.SystemMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice implements CommandExecutor {
    private final Random random = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1) {
            sender.sendMessage("Usage: /r <number-of-dice>d<dice-type> [+ additional calculations]");
            return true;
        }

        String input = args[0];
        Pattern pattern = Pattern.compile("(\\d+)d(\\d+)(([+\\-*/]\\d+)+)?");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            sender.sendMessage("Invalid format. Usage: /r <number-of-dice>d<dice-type> [+ additional calculations]");
            return true;
        }

        int numberOfDice = Integer.parseInt(matcher.group(1));
        int diceType = Integer.parseInt(matcher.group(2));
        String calculations = matcher.group(3) != null ? matcher.group(3) : "";

        int[] rolls = new int[numberOfDice];
        int sum = 0;
        StringBuilder rollDetails = new StringBuilder("(");

        for (int i = 0; i < numberOfDice; i++) {
            rolls[i] = random.nextInt(diceType) + 1;
            sum += rolls[i];
            rollDetails.append(rolls[i]);
            if (i < numberOfDice - 1) {
                rollDetails.append("+");
            }
        }

        rollDetails.append(")");

        int calculationResult = calculateResult(sum, calculations);
        StringBuilder hoverText = new StringBuilder(rollDetails);
        if (!calculations.isEmpty()) {
            hoverText.append("+(").append(calculations).append(")");
        }

        Component message = Component.text(String.valueOf(calculationResult), NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text(hoverText.toString())));


        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gray>"+
                    MiniMessage.miniMessage().serialize(player.displayName()) +
                    "<reset><gray> rolled a: </gray><white>" +
                        "<hover:show_text:'"+ hoverText +"'>"+
                    calculationResult
        ));

        return true;
    }

    private int calculateResult(int base, String calculations) {
        if (calculations.isEmpty()) {
            return base;
        }

        String expression = base + calculations;
        return evaluateExpression(expression);
    }

    private int evaluateExpression(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder operand = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                operand.append(c);
            } else {
                if (operand.length() > 0) {
                    values.push(Integer.parseInt(operand.toString()));
                    operand = new StringBuilder();
                }

                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()));
                }

                operators.push(c);
            }
        }

        if (operand.length() > 0) {
            values.push(Integer.parseInt(operand.toString()));
        }

        while (!operators.isEmpty()) {
            values.push(applyOp(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    return a;
                }
                return a / b;
        }
        return 0;
    }
}