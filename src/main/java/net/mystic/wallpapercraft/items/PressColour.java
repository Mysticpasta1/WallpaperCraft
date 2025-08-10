package net.mystic.wallpapercraft.items;

public class PressColour extends Press {

    private final String colour;

    public PressColour(final String colour) {
        super(colour);

        this.colour = colour;
    }

    public String getColour() {
        return this.colour;
    }

}
