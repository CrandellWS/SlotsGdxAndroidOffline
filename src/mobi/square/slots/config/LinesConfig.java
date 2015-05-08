package mobi.square.slots.config;

import mobi.square.slots.enums.Lines;

import com.badlogic.gdx.graphics.Color;

public class LinesConfig {

	public static final int[][] LINES_9_POSITIONS = {
		{ 1, 1, 1, 1, 1 },	// 0
		{ 0, 0, 0, 0, 0 },
		{ 2, 2, 2, 2, 2 },
		{ 0, 1, 2, 1, 0 },
		{ 2, 1, 0, 1, 2 },
		{ 0, 0, 1, 0, 0 },	// 5
		{ 2, 2, 1, 2, 2 },
		{ 1, 2, 2, 2, 1 },
		{ 1, 0, 0, 0, 1 }
	};
	public static final float[] LINES_9_COLORS = {
		Color.toFloatBits(0xff, 0x00, 0x00, 0xff),
		Color.toFloatBits(0x01, 0xfe, 0x01, 0xff),
		Color.toFloatBits(0x00, 0x00, 0xff, 0xff),
		Color.toFloatBits(0xf2, 0x64, 0x21, 0xff),
		Color.toFloatBits(0xff, 0xff, 0x00, 0xff),
		Color.toFloatBits(0xff, 0x00, 0xff, 0xff),
		Color.toFloatBits(0x00, 0xa4, 0x50, 0xff),
		Color.toFloatBits(0x00, 0xff, 0xff, 0xff),
		Color.toFloatBits(0xff, 0xff, 0xff, 0xff)
	};

	public static final int[][] LINES_20_POSITIONS = {
		{ 1, 1, 1, 1, 1 },	// 0
		{ 0, 0, 0, 0, 0 },
		{ 2, 2, 2, 2, 2 },
		{ 0, 1, 2, 1, 0 },
		{ 2, 1, 0, 1, 2 },
		{ 0, 0, 1, 0, 0 },	// 5
		{ 2, 2, 1, 2, 2 },
		{ 1, 0, 0, 0, 1 },
		{ 1, 2, 2, 2, 1 },
		{ 0, 1, 0, 1, 0 },
		{ 2, 1, 2, 1, 2 },	// 10
		{ 0, 1, 1, 1, 0 },
		{ 2, 1, 1, 1, 2 },
		{ 1, 0, 1, 0, 1 },
		{ 1, 2, 1, 2, 1 },
		{ 1, 1, 2, 1, 1 },	// 15
		{ 1, 1, 0, 1, 1 },
		{ 0, 0, 0, 1, 2 },
		{ 2, 2, 2, 1, 0 },
		{ 2, 0, 2, 0, 2 }
	};
	public static final float[] LINES_20_COLORS = {
		Color.toFloatBits(0xf9, 0x76, 0x00, 0xff),
		Color.toFloatBits(0x3f, 0x05, 0x78, 0xff),
		Color.toFloatBits(0x0c, 0xce, 0x23, 0xff),
		Color.toFloatBits(0x0b, 0x46, 0x81, 0xff),
		Color.toFloatBits(0x42, 0x05, 0xbc, 0xff),
		Color.toFloatBits(0x11, 0xc5, 0x1f, 0xff),
		Color.toFloatBits(0x78, 0x03, 0x78, 0xff),
		Color.toFloatBits(0xfa, 0xc8, 0x00, 0xff),
		Color.toFloatBits(0xf2, 0x01, 0x61, 0xff),
		Color.toFloatBits(0xc8, 0xfa, 0x01, 0xff),
		Color.toFloatBits(0xc4, 0x00, 0x31, 0xff),
		Color.toFloatBits(0x19, 0x43, 0xb4, 0xff),
		Color.toFloatBits(0xf0, 0x05, 0x05, 0xff),
		Color.toFloatBits(0x91, 0x01, 0x01, 0xff),
		Color.toFloatBits(0x07, 0x07, 0xa8, 0xff),
		Color.toFloatBits(0x00, 0x62, 0x31, 0xff),
		Color.toFloatBits(0xf9, 0xd7, 0x2f, 0xff),
		Color.toFloatBits(0x5d, 0xc5, 0xf8, 0xff),
		Color.toFloatBits(0xca, 0x32, 0x97, 0xff),
		Color.toFloatBits(0x93, 0x00, 0x31, 0xff)
	};

	public static int getIndex(Lines type, int[] positions) {
		int[][] lines = null;
		if (type == Lines.LINES_3X_09_V1) {
			lines = LINES_9_POSITIONS;
		} else if (type == Lines.LINES_3X_20_V1) {
			lines = LINES_20_POSITIONS;
		}
		if (lines != null) {
			for (int i = 0; i < lines.length; i++) {
				int[] line = lines[i];
				if (positions.length != line.length) continue;
				for (int j = 0; j < line.length; j++) {
					if (positions[j] != line[j]) break;
					if (j == line.length - 1) return i;
				}
			}
		}
		return 0;
	}

	public static float getColor(Lines type, int index) {
		float[] colors = null;
		if (type == Lines.LINES_3X_09_V1) {
			colors = LINES_9_COLORS;
		} else if (type == Lines.LINES_3X_20_V1) {
			colors = LINES_20_COLORS;
		}
		if (colors != null) {
			if (index >= 0 && index < colors.length) {
				return colors[index];
			}
		}
		return Color.WHITE.toFloatBits();
	}

}
