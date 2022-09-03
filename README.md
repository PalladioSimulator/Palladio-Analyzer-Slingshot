# The Palladio Slingshot Framework

## Modify `buildAndRun`File
This shell script file is meant to start the eclipse environment with the eclipse plugins in this project included,
so that you do not need to start Eclipse of this project first to open another Eclipse instance with the plugins.
With this, you can already start creating actual slingshot plugins such as simulators.

In order for you to use this, you need to modify the file: At the bottom, you'll find the command for starting
eclipse. However, a runtime configuration is still needed. That's why you need to first open Eclipse normally
of this project. Then create a new configuration of "Eclipse Application". Afterwards, click on "Show command-line".
Copy the command and replace it with the command in the script. Now it should work for your pc.

