package com.hebaibai.plumber;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 打印彩色日志工具
 */
public class Style {

    private static final List<FontColor> FONT_COLORS = Arrays.asList(
            FontColor.BLACK,
            FontColor.RED,
            FontColor.GREEN,
            FontColor.YELLOW,
            FontColor.BLACK,
            FontColor.FUCHSIN,
            FontColor.CYAN,
            FontColor.WHITE,
            FontColor.NORMAL
    );
    private static final List<BackgroundColor> BACKGROUND_COLORS = Arrays.asList(
            BackgroundColor.BLACK,
            BackgroundColor.RED,
            BackgroundColor.GREEN,
            BackgroundColor.YELLOW,
            BackgroundColor.BLACK,
            BackgroundColor.FUCHSIN,
            BackgroundColor.CYAN,
            BackgroundColor.WHITE,
            BackgroundColor.NORMAL
    );
    private String fmort;

    private Style() {
    }

    public static Style style(FontColor font, BackgroundColor background, Border border, Font fontStyle) {
        Style style = new Style();
        style.fmort = "\33[" + font.val + ";" + background.val + ";" + border.val + ";" + fontStyle.val + "[TEXT]\33[0m";
        return style;
    }

    public static Style style(FontColor font, BackgroundColor background) {
        return style(font, background, Border.NO, Font.NORMAL);
    }

    public static Style style(FontColor font) {
        return style(font, BackgroundColor.NORMAL, Border.NO, Font.NORMAL);
    }

    public static Style random() {
        int fontColorIndex = new Random().nextInt(FONT_COLORS.size());
        FontColor fontColor = FONT_COLORS.get(fontColorIndex);
        return Style.style(fontColor, BackgroundColor.NORMAL, Border.NO, Font.BOLD);
    }

    public static Style error() {
        return Style.style(FontColor.RED, BackgroundColor.NORMAL, Border.YES, Font.BOLD);
    }

    public String str(String string) {
        return fmort.replace("[TEXT]", string);
    }

    enum Font {
        //加粗
        BOLD("1m"),
        //正常
        NORMAL("2m"),
        //斜体
        ITALICS("3m"),
        //下划线
        UNDERLINE("4m"),
        //反色
        ANTI_COLOR("7m");
        private final String val;

        Font(String val) {
            this.val = val;
        }

    }

    public enum Border {
        //黑
        YES("52"),
        //红
        NO("50");
        private final String val;

        Border(String val) {
            this.val = val;
        }
    }

    public enum FontColor {
        //黑
        BLACK("30"),
        //红
        RED("31"),
        //绿
        GREEN("32"),
        //黄
        YELLOW("33"),
        //蓝
        BLUE("34"),
        //品红
        FUCHSIN("35"),
        //青色
        CYAN("36"),
        //白
        WHITE("37"),
        //正常
        NORMAL("50");
        private final String val;

        FontColor(String val) {
            this.val = val;
        }
    }

    public enum BackgroundColor {
        //黑
        BLACK("40"),
        //红
        RED("41"),
        //绿
        GREEN("42"),
        //黄
        YELLOW("43"),
        //蓝
        BLUE("44"),
        //品红
        FUCHSIN("45"),
        //青色
        CYAN("46"),
        //白
        WHITE("47"),
        //正常
        NORMAL("50");
        private final String val;

        BackgroundColor(String val) {
            this.val = val;
        }
    }
}
