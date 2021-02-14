#!/bin/bash
SPATH=$(dirname "$(readlink -f "$0")")
mkdir -p $SPATH/server-spigot/

PLUGINS=$SPATH/server-spigot/plugins
mkdir -p $PLUGINS

function dl() {
  curl -LO ${2} --output-dir ${1}
}
dl $SPATH/server-spigot/ https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/468/downloads/paper-1.16.5-468.jar
[ -f $SPATH/server-spigot/download ] && mv $SPATH/server-spigot/download $SPATH/server-spigot/server.jar
dl $PLUGINS https://github.com/tr7zw/Item-NBT-API/releases/download/2.6.0/item-nbt-api-plugin-2.6.0.jar
dl $PLUGINS https://media.forgecdn.net/files/3108/83/EffectLib-6.4.jar
dl $PLUGINS https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/target/ProtocolLib.jar
[ -f $SPATH/server-spigot/eula.txt ] || echo "eula=true" >> $SPATH/server-spigot/eula.txt
