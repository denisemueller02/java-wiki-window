# Minecraft Wiki Lookup Mod

A custom Minecraft mod developed for use in a VR-based user study. It allows players to access the Minecraft Wiki in-game based on the item, block, or entity they are interacting with.

## 📦 Version & Requirements

- **Minecraft Java**: 1.21.4  
- **Mod Loader**: [Fabric](https://fabricmc.net/)  
- **Template Used**: [Fabric Template](https://fabricmc.net/develop/template/)

## 🧪 Research Context

This mod was developed as part of a user study on immersion, presence, and interactivity in VR environments using Minecraft. It was tested by experienced players before inclusion in the experiment.

## 🔧 Features

- **Entity and Block Lookup**  
  Right-click an entity or block while holding the custom item to open the corresponding Minecraft Wiki page.

- **Liquid Blocks (Water/Lava)**  
  - Using the item on water or lava opens the appropriate page.  
  - *Sneaking* bypasses the liquid to target what’s behind it.  
  - Waterlogged blocks open the water page unless sneaking.

- **Usable Blocks (e.g., Chests, Crafting Tables)**  
  *Sneak* while using the item to avoid activating the block and open its wiki page.

- **Item Lookup**  
  - Place the wiki opener in the **off-hand**.  
  - Hold the target item in the **main hand**.  
  - *Sneak* if the item is usable; otherwise, right-click in empty space.

- **Language Support**  
  - If the game language is set to `Deutsch (Deutschland)`, the mod uses `de.minecraft.wiki`.  
  - For `English (UK)`, it uses `minecraft.wiki`.  
  - Item names are translated accordingly.


## 📜 License

This project was created for academic research purposes. Licensing terms may apply depending on the final publication.
