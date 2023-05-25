# BetterGiveaways 2 - Fork Version

![](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fkartonixon%2Fbetter-giveaways&count_bg=%2379C83D&title_bg=%23555555&icon=github.svg&icon_color=%23E7E7E7&title=visits&edge_flat=false)
![](https://img.shields.io/spiget/downloads/83231)
![](https://img.shields.io/github/issues/kartonixon/better-giveaways)
![](https://img.shields.io/github/license/kartonixon/better-giveaways)

> Simple and easy to use Minecraft giveaway system
> Made by kartonixon, recoded by santicsls

A Minecraft Spigot plugin that allows players to create simple giveaways. The winner will always be chosen at random, making the giveaways fair for participants.

Share your thoughts at [SpigotMC](https://www.spigotmc.org/resources/bettergiveaways.83231/) ❤

Tested Minecraft Versions: **1.16, 1.18.2 (LATEST BUILD)**

## Installation

- Download .jar file
- Make sure that you are using a Spigot Minecraft server running one of the supported versions
- Put the .jar file in the `/plugins` folder
- Start the server
- Enjoy!

## Commands

Command | Description
------- | -----------
`/giveaway` | Does not require any permissions. Joins the ongoing giveaway.
`/giveaway start` | Requires `bettergiveaways.manage` permission. Starts the giveaway. 
`/giveaway end` | Requires `bettergiveaways.manage` permission. Ends the giveaway and randomly picks a winner.
`/giveaway list` | Requires `bettergiveaways.manage` permission. Displays a list of players that joined the giveaway.
`/giveaway reload` | Requires `bettergiveaways.manage` permission. Reload config.

## Config 

You can customize almost all messages sent by this plugin in `custom.yml` file, that generates automagically inside the `/plugins/BetterGiveaways` folder. Config supports usual Minecraft-style text formatting.

## Technologies used
- Spigot API
- Java

## Contributing

If you have any **issues** with this plugin or **ideas** on how to improve it, please use GitHub Issues section! Thank you ❤
