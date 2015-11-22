#TeamCity Phabricator Plugin
Real-time build triggers and reporting with JetBrain's TeamCity and Phacility's Phabricator (Harbormaster)

##Installation
The plugin consists of two pieces: the TeamCity Java plugin (Build Feature) which itself consists of two parts: a Server and Agent plugin, as well as the custom Phabricator Harbormaster Build Step.

###TeamCity
Installing the TeamCity plugin is extremely easy. In the latest release, unzip the release and in the TeamCity folder is `teamcity-phabricator-plugin.zip`. This zip is uploaded directly to your TeamCity instance. The plugin is automatically applied to all of your available Agents but requires a reboot to apply the Server side of the plugin.

After you install and bounce the Server, you'll now be able to apply a custom Build Feature, "Phabricator Plugin". After selecting the plugin, you should be presented with the following prompt:

![Phabricator Plugin Prompt]
(https://i.imgur.com/KArV0GP.png)

***Phabricator URL***: The domain of your Phabricator installation, e.g. phabricator.example.com

***Conduit Token***: The Conduit API Key for the user you want to be executing build actions. This probably should be a Phabricator Bot as a best practice.

***Path To Anarcist***: This is the path to Anarcist on your build agents. Currently, the plugin relies on a hardcoded path the Anarcist. In the future, this will simply look for `arc`.

After you successfully fill out the preceding options, you need to make a change to your VCS Checkout Settings for the project you plan on using the plugin with.

Change the `Checkout Options` to `Automatically On Agent`. This enables us to execute actions against
our checked out code (clean, reset) and apply Differential patches without lock errors

![Checkout Change]
(https://i.imgur.com/IFBbx0s.png)

###Phabricator
Installing the Phabricator piece is a little bit more involved but still pretty easy. In the
Phabricator folder from the release zip, there are two files (1) `HarbormasterTeamCityBuildStepImplementation.php`
and (2)`TeamCityXmlBuildBuilder.xml`. Move these files into your Phabricator's `src/extensions` directory.
They will automatically be consumed by Phabricator and be ready to use.

Now you're ready to add TeamCity into your Harbormaster Build workflow. The most common use case is to
create a new Herold rule that, on every diff pushed, runs any number of given build plans. When adding
the new TeamCity Build Plan, you'll be presented with the following options:

![Build Step]
(https://i.imgur.com/8aUgk5q.png)

***URI***: The URL to your TeamCity instance.
***TeamCity Build Configuration ID***: This is the ID given to your project when it is initially created.
![BuildId]
(https://i.imgur.com/P9hOc5s.png)
***TeamCity Credentials***: You must add a set of credentials (username/password) that has access to your
TeamCity installation in order to make RESTful calls.

After all of that is complete, add the Build Step to a Herold rule for any of your projects, push a diff to
said project and watch the magic happen!

##Functionality
The TeamCity plugin currently reports the following to Phabricator (Harbormaster):

1. Build Pass/Fail on completion
2. Any Unit Tests you have defined in your build steps and their pass/fail status

##Future Features

1. Add more detailed Unit Test reporting, like duration
2. Add lint test reporting
3. Add Differential commenting

##Contributions
I want to give a special shoutout to @sectioneight for the inspiration on how to scaffold out this plugin
(He wrote a majority of [Uber's Jenkins->Phabricator plugin|https://github.com/uber/phabricator-jenkins-plugin]) and @joprice for moral support
