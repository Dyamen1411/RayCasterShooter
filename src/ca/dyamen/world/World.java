package ca.dyamen.world;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class World {
    private int width;
    private int height;
    private int color_count;
    private byte[] data;
    private float[] colors;

    public World() {

    }

    private static int bytesToInt(byte[] bytes, int index) {
        return bytes[index + 0] | bytes[index + 1] << 8 | bytes[index + 2] << 16 | bytes[index + 3] << 24;
    }

    public void loadWorld(String path) throws Exception {
        File world_file = new File(path);
        byte[] world_file_data = new byte[(int) world_file.length()];

        DataInputStream dis = new DataInputStream(new FileInputStream(world_file));
        dis.read(world_file_data);
        dis.close();

        // Read constants
        width = bytesToInt(world_file_data, 0);
        height = bytesToInt(world_file_data, 4);
        color_count = bytesToInt(world_file_data, 8);

        // Read world data
        //data = Arrays.copyOfRange(world_file_data, 12, 12 + width*height);
        data = new byte[width*height];
        for (int i = 12, j = 0; j < data.length; ++i, ++j) {
            data[j] = world_file_data[i];
        }

        // Read color data
        colors = new float[color_count * 3];
        final int offset = 12 + data.length;
        for (int i = offset, j = 0; i < offset + colors.length; i += 3, j += 3) {
            colors[j+0] = (float) (((float) (world_file_data[i+0] & 0xFF)) / 255.);
            colors[j+1] = (float) (((float) (world_file_data[i+1] & 0xFF)) / 255.);
            colors[j+2] = (float) (((float) (world_file_data[i+2] & 0xFF)) / 255.);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColorCount() {
        return color_count;
    }

    public byte[] getData() {
        return data;
    }

    public float[] getColors() {
        return colors;
    }
}
