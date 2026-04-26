# infinite-brute

Minecraft fabric mod for 20w14infinite, that just adds some helpful
commands for searching dimensions.

> [!WARNING]
> This mod is Work Is (not) in Progress. Its actually slow and hangs sometimes.

Commands:
- `/diminfo [id]` - view information about dimension by its numeric
  ID.
- `/dimhash [string]` - hash the string and print the dimension ID
  this would lead to. also has a button to run `/diminfo` for
  convenience.
- `/biomeinfo [id]` - view info about a biome by its numeric id.
- `/getcurrent dimension|biome` - view info about current
  dimension/biome
- `/dimbrutelength [number]` - set the length of the book contents. note
  the size is in bytes before base64 is applied, so may not be correct
  (it wont). Default is 3
- `/dimbrutereset` - start next `/dimbrute` from the beginning,
  instead of continuing where it left off.
- `/dimbrute` - search for *something* in dimensions, giving you a
  book on success. repeated runs will continue where it ended instead
  of starting over. when it reaches the end it fails and running again
  would start from beginning.

More specifically about `/dimbrute`:
- `/dimbrute block ...` - search for a block. it will never find a POI
  block or a block with Block Entity, due to being unfeasible to find.
- `/dimbrute biome ...` - search for biomes by their names. it finds
  dimensions containing the biome without guaranteeing the biome is
  actually spawned by the game.
- it makes a byte array of your specified size and then turns it into
  a string using base64. base64 allows it to guarantee you can
  actually type it into a book.

# License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

See `LICENSE` file for full copy of the license.
