package com.prjmoose.treeless;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Map {
	public static class Tile {
		public static byte TYPE_NORMAL = 0;
		public static byte TYPE_SPAWN = 1;
		
		public static short FLAG_ENABLED = 1;
		public static short FLAG_WALKABLE = 2;
		public static short FLAG_FLYABLE = 4;
		public static short FLAG_SWIMABLE = 8;
		public static short FLAG_SAILABLE = 16;
		public static short FLAG_6 = 32;
		public static short FLAG_7 = 64;
		public static short FLAG_8 = 128;
		public static short FLAG_9 = 256;
		public static short FLAG_10 = 512;
		public static short FLAG_11 = 1024;
		public static short FLAG_12 = 2048;
		public static short FLAG_13 = 4096;
		public static short FLAG_14 = 8192;
		public static short FLAG_15 = 16384;
		
		private byte type;
		private short flags;
		private byte spriteIndex;
		
		public Tile() {
			type = TYPE_NORMAL;

			if((Math.random() * 100) < 90) {
				flags = (short) (FLAG_ENABLED | FLAG_WALKABLE | FLAG_FLYABLE);
				spriteIndex = 0;
			} else {
				flags = (short) (FLAG_ENABLED | FLAG_FLYABLE | FLAG_SAILABLE);
				spriteIndex = 1;
			}
		}
		
		public Tile(int tileData) {
			type = (byte) (tileData >> 24);
			flags = (short) ((tileData >> 8) & 0xFFFF);
			spriteIndex = (byte) (tileData & 0xFF);
		}

		public byte getType() {
			return type;
		}

		public short getFlags() {
			return flags;
		}

		public byte getSpriteIndex() {
			return spriteIndex;
		}
	}

	private int width;
	private int height;
	
	private int tileSize;

	private Tile[][] map;
	
	private String spriteSheetPath;
	
	public Map(String mapName) {
		// read header from file
		// read tiles from file
		
		// for now just draw a static map
		width = 384;
		height = 384;
		tileSize = 16;

		map = new Tile[height][width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				map[y][x] = new Tile();
			}
		}
		
		spriteSheetPath = "basic16";
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileSize() {
		return tileSize;
	}
	
	public BufferedImage[] loadSpriteSheet() throws IOException {
		BufferedImage spriteSheet;
		BufferedImage[] sprites;
		
		spriteSheet = ImageIO.read(getClass().getResource("/com/prjmoose/treeless/data/sprites/maps/" + spriteSheetPath + ".png"));
		sprites = new BufferedImage[(spriteSheet.getWidth() / tileSize) * (spriteSheet.getHeight() / tileSize)];
		
		for(int row = 0; row < spriteSheet.getHeight() / tileSize; row++) {
			for(int col = 0; col < spriteSheet.getWidth() / tileSize; col++) {
				sprites[row * (spriteSheet.getWidth() / tileSize) + col] = spriteSheet.getSubimage(col * tileSize, row * tileSize, tileSize, tileSize);
			}
		}
		
		return sprites;
	}

	public Tile getTileAt(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return null; }
		
		return map[y][x];
	}
}
