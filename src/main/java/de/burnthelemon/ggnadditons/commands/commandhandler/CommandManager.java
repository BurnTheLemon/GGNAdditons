package de.burnthelemon.ggnadditons.commands.commandhandler;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final Map<String, Object> commandInstances = new HashMap<>();
    private final Map<String, Map<List<String>, Method>> commandMethods = new HashMap<>();
    private final Map<Method, String> commandPermissions = new HashMap<>();
    private final Map<Method, Boolean[]> commandSources = new HashMap<>();
    private final Map<String, Method> defaultCommands = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(Class<?> clazz) {
        try {
            CommandName commandName = clazz.getAnnotation(CommandName.class);
            if (commandName == null) return;

            Object commandInstance = clazz.getDeclaredConstructor().newInstance();
            commandInstances.put(commandName.value(), commandInstance);

            Method defaultMethod = null;

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals("onDefaultCommandExecution")) {
                    defaultMethod = method;
                }

                if (method.isAnnotationPresent(SubCMD.class)) {
                    SubCMD subCMD = method.getAnnotation(SubCMD.class);
                    CMDPermission permission = method.getAnnotation(CMDPermission.class);
                    CommandSource source = method.getAnnotation(CommandSource.class);

                    List<String> key = new ArrayList<>();
                    key.add(commandName.value());
                    key.addAll(Arrays.asList(subCMD.value()));

                    commandMethods.computeIfAbsent(commandName.value(), k -> new HashMap<>()).put(key, method);

                    if (permission != null) {
                        commandPermissions.put(method, permission.value());
                    }

                    if (source != null) {
                        commandSources.put(method, new Boolean[]{source.console(), source.player(), source.commandBlock()});
                    }
                }
            }

            if (defaultMethod != null) {
                defaultCommands.put(commandName.value(), defaultMethod);
                CMDPermission permission = clazz.getAnnotation(CMDPermission.class);
                CommandSource source = clazz.getAnnotation(CommandSource.class);

                if (permission != null) {
                    commandPermissions.put(defaultMethod, permission.value());
                }

                if (source != null) {
                    commandSources.put(defaultMethod, new Boolean[]{source.console(), source.player(), source.commandBlock()});
                }
            }

            plugin.getCommand(commandName.value()).setExecutor(this);
            plugin.getCommand(commandName.value()).setTabCompleter(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<List<String>, Method> methods = commandMethods.get(label);
        Method method = null;

        if (methods != null) {
            List<String> cmdKey = new ArrayList<>();
            cmdKey.add(label);
            Collections.addAll(cmdKey, args);

            for (int i = cmdKey.size(); i > 0; i--) {
                List<String> subCmdKey = cmdKey.subList(0, i);
                method = methods.get(subCmdKey);
                if (method != null) break;
            }
        }

        if (method == null) {
            method = defaultCommands.get(label);
        }

        if (method == null) return false;

        if (!(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) && !checkPermission(sender, method)) {
            sender.sendMessage("You do not have permission to execute this command.");
            return true;
        }

        if (!checkSource(sender, method)) {
            sender.sendMessage("This command cannot be executed from this source.");
            return true;
        }

        try {
            method.setAccessible(true);
            if (sender instanceof Player) {
                method.invoke(commandInstances.get(label), (Player) sender);
            } else {
                method.invoke(commandInstances.get(label), sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkPermission(CommandSender sender, Method method) {
        String permission = commandPermissions.get(method);
        return permission == null || sender.hasPermission(permission);
    }

    private boolean checkSource(CommandSender sender, Method method) {
        Boolean[] sources = commandSources.get(method);
        if (sources == null) return true;

        if (sender instanceof Player && sources[1]) return true;
        if (sender instanceof ConsoleCommandSender && sources[0]) return true;
        if (sender instanceof BlockCommandSender && sources[2]) return true;
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList(); // Implement tab completion if necessary
    }
}
