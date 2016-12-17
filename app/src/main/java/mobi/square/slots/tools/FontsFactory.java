package mobi.square.slots.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.dl.FilesList;

public class FontsFactory {

    private static final ExecutorService service;
    private static final Map<String, Map<Integer, BitmapFont>> fonts;
    private static final Map<String, Map<Integer, Future<BitmapFont>>> futures;
    private static final Map<String, Map<Float, FontBitmap>> bitmap_size;

    static {
        fonts = new HashMap<String, Map<Integer, BitmapFont>>();
        futures = new HashMap<String, Map<Integer, Future<BitmapFont>>>();
        bitmap_size = new HashMap<String, Map<Float, FontBitmap>>();
        service = Executors.newFixedThreadPool(1);
    }

    /**
     * Возвращает шрифт указанного размера.<br>
     * <b>Генерирует</b> шрифт, если его не существует.
     *
     * @param name - имя файла со шрифтом из папки <b>fonts</b>;
     * @param size - размер шрифта в мировых координатах.
     * @return {@link BitmapFont}
     */
    @Deprecated
    public static BitmapFont get(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        Integer size_i = getPixelsFontSize(size);
        BitmapFont font = map.get(size_i);
        if (font == null) {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(getFontFile(name));
            FreeTypeFontParameter p = new FreeTypeFontParameter();
            p.characters = AppConfig.FONT_SYMBOLS;
            p.size = size_i.intValue();
            font = gen.generateFont(p);
            font.setUseIntegerPositions(true);
            gen.dispose();
        }
        return font;
    }

    /**
     * Выгрузить все созданные шрифты из памяти.
     */
    public static void dispose() {
        Set<String> names = fonts.keySet();
        for (String name : names) {
            Map<Integer, BitmapFont> map = fonts.get(name);
            if (map == null) continue;
            Set<Integer> sizes = map.keySet();
            for (Integer size : sizes) {
                BitmapFont font = map.get(size);
                if (font != null) font.dispose();
            }
            map.clear();
        }
        fonts.clear();
    }

    /**
     * Асинхронно загружает указанный шрифт.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    public static void loadAsync(String name, float size) {
        if (name == null) return;
        if (name.endsWith(".fb")) {
            loadAsyncFnt(name, size);
        } else {
            loadAsyncTtf(name, size);
        }
    }

    /**
     * Возвращает шрифт, если он загружен, иначе возвращает null.<br>
     * Если шрифт в процессе загрузки - возвращает null.
     *
     * @param name - имя файла со шрифтом из папки fonts;
     * @param size - размер шрифта.
     * @return {@link BitmapFont} или <b>null</b>
     */
    public static BitmapFont getAsync(String name, float size) {
        if (name == null) return null;
        BitmapFont font = name.endsWith(".fb") ? getAsyncFnt(name, size) : getAsyncTtf(name, size);
        //if (font == null) System.out.println("Not loaded: " + name + " (" + size + ")");
        return font == null ? get(name, size) : font;
    }

    /**
     * Загружен ли шрифт.
     *
     * @param name - имя файла со шрифтом из папки fonts;
     * @param size - размер шрифта.
     * @return true/false
     */
    public static boolean isLoaded(String name, float size) {
        if (name == null) return false;
        BitmapFont font = name.endsWith(".fb") ? getAsyncFnt(name, size) : getAsyncTtf(name, size);
        return font != null;
    }

    /**
     * Выгружает шрифт из памяти или отменяет его загрузку.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    public static void unload(String name, float size) {
        if (name == null) return;
        if (name.endsWith(".fb")) {
            unloadFnt(name, size);
        } else {
            unloadTtf(name, size);
        }
    }

    /**
     * Асинхронно загружает указанный шрифт FNT.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    private static void loadAsyncFnt(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        FontBitmap info = getBitmapInfo(name, size);
        Integer size_i = Integer.valueOf(info.size);
        BitmapFont font = map.get(size_i);
        if (font == null) {
            /*Map<Integer, Future<BitmapFont>> f_map = getFuturesMap(name);
			Future<BitmapFont> future = f_map.get(size_i);
			if (future == null) {
				future = service.submit(new BitmapFontLoader(name, info.name, size_i));
				f_map.put(size_i, future);
			}*/
            Gdx.app.postRunnable(new BitmapFontRenderer(name, info.name, size_i));
        }
    }

    /**
     * Асинхронно загружает указанный шрифт TTF.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    private static void loadAsyncTtf(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        Integer size_i = getPixelsFontSize(size);
        //System.out.println("Load " + name + " (" + size + ") with size " + size_i);
        BitmapFont font = map.get(size_i);
        if (font == null) {
            Map<Integer, Future<BitmapFont>> f_map = getFuturesMap(name);
            Future<BitmapFont> future = f_map.get(size_i);
            if (future == null) {
                future = service.submit(new FontLoader(name, size_i));
                f_map.put(size_i, future);
            }
        }
    }

    /**
     * Возвращает шрифт TTF, если он загружен, иначе возвращает null.<br>
     * Если шрифт в процессе загрузки - возвращает null.
     *
     * @param name - имя файла со шрифтом из папки fonts;
     * @param size - размер шрифта.
     * @return {@link BitmapFont} или <b>null</b>
     */
    private static BitmapFont getAsyncTtf(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        Integer size_i = getPixelsFontSize(size);
        return map.get(size_i);
    }

    /**
     * Возвращает шрифт FNT, если он загружен, иначе возвращает null.<br>
     * Если шрифт в процессе загрузки - возвращает null.
     *
     * @param name - имя файла со шрифтом из папки fonts;
     * @param size - размер шрифта.
     * @return {@link BitmapFont} или <b>null</b>
     */
    private static BitmapFont getAsyncFnt(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        FontBitmap info = getBitmapInfo(name, size);
        Integer size_i = Integer.valueOf(info.size);
        return map.get(size_i);
    }

    /**
     * Выгружает шрифт TTF из памяти или отменяет его загрузку.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    private static void unloadTtf(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        Integer size_i = getPixelsFontSize(size);
        BitmapFont font = map.get(size_i);
        if (font == null) {
            Map<Integer, Future<BitmapFont>> f_map = getFuturesMap(name);
            Future<BitmapFont> future = f_map.get(size_i);
            if (future != null) {
                future.cancel(false);
                f_map.remove(size_i);
            }
        } else map.remove(size_i);
    }

    /**
     * Определяет пиксельный размер шрифта.
     *
     * @param size - размер шрифта.
     * @return Integer
     */
    private static Integer getPixelsFontSize(float size) {
        float ppu = (float) AppConfig.VIEWPORT_HEIGHT / (float) Gdx.graphics.getHeight();
		/*if (Gdx.graphics.getWidth() > 1024) {
			int width = Gdx.graphics.getWidth();
			float a = (float)(width - 1024);
			float b = .0002f;
			float c = 1f - b * a;
			if (c < .7f) c = .7f;
			if (c > 1f) c = 1f;
			size *= c;
		}*/
        Integer size_i = Integer.valueOf((int) (size / ppu));
        return size_i;
    }

    /**
     * Выгружает шрифт FNT из памяти или отменяет его загрузку.
     *
     * @param name - имя шрифта из папки fonts;
     * @param size - размер шрифта.
     */
    private static void unloadFnt(String name, float size) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        FontBitmap info = getBitmapInfo(name, size);
        Integer size_i = Integer.valueOf(info.size);
        BitmapFont font = map.get(size_i);
        if (font == null) {
            Map<Integer, Future<BitmapFont>> f_map = getFuturesMap(name);
            Future<BitmapFont> future = f_map.get(size_i);
            if (future != null) {
                future.cancel(false);
                f_map.remove(size_i);
            }
        } else map.remove(size_i);
    }

    /**
     * Возвращает файл по имени шрифта.<br>
     * То же самое, что <b>FileHandleResolver</b>.<br>
     * Если шрифт не найден, возвращает Ариал.
     *
     * @param name - имя шрифта.
     * @return {@link FileHandle}
     */
    private static FileHandle getFontFile(String name) {
        if (name == null || name.isEmpty())
            return Gdx.files.internal("fonts/arial.ttf");
        if (name.contains("/") || name.contains("\\"))
            return Gdx.files.external(FilesList.INSTALL_PATH.concat(name));
        return Gdx.files.internal("fonts/".concat(name));
    }

    // Private Methods

    protected static void putFont(String name, Integer size, BitmapFont font) {
        Map<Integer, BitmapFont> map = getFontsMap(name);
        map.put(size, font);
        Map<Integer, Future<BitmapFont>> f_map = getFuturesMap(name);
        f_map.remove(size);
    }

    public static FontBitmap getBitmapInfo(String name, float size) {
        Map<Float, FontBitmap> map = bitmap_size.get(name);
        if (map == null) {
            map = new HashMap<Float, FontsFactory.FontBitmap>();
            bitmap_size.put(name, map);
        }
        Float size_f = Float.valueOf(size);
        FontBitmap info = map.get(size_f);
        if (info == null) {
            FontBitmap[] sizes = loadSizeList(name);
            Arrays.sort(sizes, new Comparator<FontBitmap>() {
                @Override
                public int compare(FontBitmap f1, FontBitmap f2) {
                    if (f1.size > f2.size) {
                        return 1;
                    } else if (f1.size < f2.size) {
                        return -1;
                    } else return 0;
                }
            });
            float target = size / (float) AppConfig.VIEWPORT_HEIGHT * (float) Gdx.graphics.getHeight();
            for (int i = 0; i < sizes.length; i++) {
                if (i == sizes.length - 1) return sizes[i];
                if ((float) sizes[i].size > target) return sizes[i];
                if ((float) sizes[i + 1].size < target) continue;
                float d1 = target - (float) sizes[i].size;
                float d2 = (float) sizes[i + 1].size - target;
                return d1 > d2 ? sizes[i + 1] : sizes[i];
            }
            return null;
        } else return info;
    }

    private static FontBitmap[] loadSizeList(String name) {
        FileHandle file = getFontFile(name);
        String content = file.readString();
        String[] lines = content.split("\n");
        List<FontBitmap> list = new LinkedList<FontBitmap>();
        for (String line : lines) {
            line = line.trim().replaceAll(" ", "");
            if (line.startsWith("//")) continue;
            String[] chunks = line.split("=");
            if (chunks.length != 2) continue;
            list.add(new FontBitmap(
                    chunks[1].trim(),
                    Integer.parseInt(chunks[0].trim())
            ));
        }
        return list.toArray(new FontBitmap[0]);
    }

    private static Map<Integer, BitmapFont> getFontsMap(String name) {
        Map<Integer, BitmapFont> map = fonts.get(name);
        if (map == null) {
            map = new HashMap<Integer, BitmapFont>();
            fonts.put(name, map);
        }
        return map;
    }

    private static Map<Integer, Future<BitmapFont>> getFuturesMap(String name) {
        Map<Integer, Future<BitmapFont>> map = futures.get(name);
        if (map == null) {
            map = new HashMap<Integer, Future<BitmapFont>>();
            futures.put(name, map);
        }
        return map;
    }

    protected static class FontBitmap {
        public final int size;
        public final String name;

        public FontBitmap(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    protected static class FontRenderer implements Runnable {
        private final String name;
        private final Integer size;
        private final FreeTypeFontGenerator generator;
        private final FreeTypeFontParameter parameter;

        public FontRenderer(FreeTypeFontGenerator g, FreeTypeFontParameter p, String name, int size) {
            this.generator = g;
            this.parameter = p;
            this.name = name;
            this.size = Integer.valueOf(size);
        }

        @Override
        public void run() {
            BitmapFont font = this.generator.generateFont(this.parameter);
            font.setUseIntegerPositions(true);
            this.generator.dispose();
			/*TextureRegion[] regions = font.getRegions();
			for (TextureRegion region : regions) {
				region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}*/
            FontsFactory.putFont(this.name, this.size, font);
        }
    }

    protected static class FontLoader implements Callable<BitmapFont> {
        private final String name;
        private final Integer size;

        public FontLoader(String name, Integer size) {
            this.name = name;
            this.size = size;
        }

        @Override
        public BitmapFont call() throws Exception {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(getFontFile(this.name));
            FreeTypeFontParameter p = new FreeTypeFontParameter();
            p.characters = AppConfig.FONT_SYMBOLS;
            p.size = this.size.intValue();
            Gdx.app.postRunnable(new FontRenderer(gen, p, this.name, this.size));
            return null;
        }
    }

    protected static class BitmapFontRenderer implements Runnable {
        private final String name;
        private final Integer size;
        private final String batch;

        public BitmapFontRenderer(String batch, String name, Integer size) {
            this.batch = batch;
            this.name = name;
            this.size = size;
        }

        @Override
        public void run() {
            BitmapFont font = new BitmapFont(getFontFile(this.name));
			/*TextureRegion[] regions = font.getRegions();
			for (TextureRegion region : regions) {
				region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}*/
            FontsFactory.putFont(this.batch, this.size, font);
        }
    }

    protected static class BitmapFontLoader implements Callable<BitmapFont> {
        private final String name;
        private final Integer size;
        private final String batch;

        public BitmapFontLoader(String batch, String name, Integer size) {
            this.batch = batch;
            this.name = name;
            this.size = size;
        }

        @Override
        public BitmapFont call() throws Exception {
            Gdx.app.postRunnable(new BitmapFontRenderer(this.batch, this.name, this.size));
            return null;
        }
    }

}
