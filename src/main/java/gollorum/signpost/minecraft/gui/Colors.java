package gollorum.signpost.minecraft.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Function;

public class Colors {

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#ffffff;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int white = 0xffffff;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#999999;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int grey = 0x999999;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#e0e0e0;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int lightGrey = 0xe0e0e0;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#707070;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int darkGrey = 0x707070;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#000000;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int black = 0x000000;
    public static final int highlight = TextFormatting.AQUA.getColor();

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#ffffff;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int valid = white;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#dddddd;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int validInactive = 0xdddddd;


    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#ff444444;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int invalid = 0xff4444;

    /**
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#dd6666;float:right;margin: 0 10px 0 0"></div><br/><br/>
     */
    public static final int invalidInactive = 0xdd6666;

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00ffffff) + alpha << 24;
    }
    public static int withRed(int color, int red) {
        return (color & 0xff00ffff) + red << 16;
    }
    public static int withGreen(int color, int green) {
        return (color & 0xffff00ff) + green << 8;
    }
    public static int withBlue(int color, int blue) {
        return (color & 0xffffff00) + blue;
    }

    public static int withAlpha(int color, Function<Integer, Integer> alphaMapping) {
        return withAlpha(color, alphaMapping.apply(getAlpha(color)));
    }
    public static int withRed(int color, Function<Integer, Integer> redMapping) {
        return withRed(color, redMapping.apply(getRed(color)));
    }
    public static int withGreen(int color, Function<Integer, Integer> greenMapping) {
        return withGreen(color, greenMapping.apply(getGreen(color)));
    }
    public static int withBlue(int color, Function<Integer, Integer> blueMapping) {
        return withBlue(color, blueMapping.apply(getBlue(color)));
    }

    public static int map(int color, Function<Integer, Integer> mapping) {
        return from(mapping.apply(getRed(color)), mapping.apply(getGreen(color)), mapping.apply(getBlue(color)));
    }

    public static int from(int red, int green, int blue) {
        return (red << 16) + (green << 8) + blue;
    }

    public static int getAlpha(int color) { return (color >>> 24) & 0xff; }
    public static int getRed(int color) { return (color >>> 16) & 0xff; }
    public static int getGreen(int color) { return (color >>> 8) & 0xff; }
    public static int getBlue(int color) { return color & 0xff; }

    public static ITextComponent wrap(String text, int color) {
        StringTextComponent ret = new StringTextComponent(text);
        ret.setStyle(ret.getStyle().setColor(net.minecraft.util.text.Color.fromInt(color)));
        return ret;
    }

}
