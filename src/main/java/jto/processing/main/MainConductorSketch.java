package jto.processing.main;


import jto.processing.sketch.BouncyBallSketch;
import jto.processing.sketch.ConductableSketch;
import jto.processing.sketch.Sketch;
import jto.processing.surface.mapper.SurfaceMapperGui;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.net.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainConductorSketch extends PApplet {
    private SurfaceMapperGui surfaceMapperGui;
    private static String ipAddress;
    private Client client;


    @Override
    public void draw() {
        if (client.available() > 0) {
            String data = client.readStringUntil('\n');
            if (null == data) {
                return;
            }
            String[] values = data.split(",");
            PVector velocity = new PVector(Float.valueOf(values[0]), Float.valueOf(values[1]));
            for (Sketch sketch : surfaceMapperGui.getSketchList()) {
                if (sketch instanceof BouncyBallSketch) {
                    BouncyBallSketch bouncyBallSketch = (BouncyBallSketch) sketch;
                    bouncyBallSketch.updateBallVectors(velocity);
                }
            }
        }
        surfaceMapperGui.draw();
        float rand = random(1);
        if (rand < 0.04) {
            for (Sketch sketch : surfaceMapperGui.getSketchList()) {
                if (sketch instanceof BouncyBallSketch) {
                    BouncyBallSketch bouncyBallSketch = (BouncyBallSketch) sketch;
                    bouncyBallSketch.randomize();
                }
            }
        }
    }

    @Override
    public void setup() {
        // URL url = Thread.currentThread().getContextClassLoader().getResource("IMG_1028.JPG");
        // PImage image = loadImage(url.getFile());

        //image.resize(image.width / 2, image.height / 2);

        //size(image.width, image.height, PConstants.OPENGL);
        size(800, 600, PConstants.OPENGL);

        Conductor conductor = new Conductor(this);
        conductor.setup();

        surfaceMapperGui = new SurfaceMapperGui(this);
        for (ConductableSketch conductableSketch : conductor.getSketchList()) {
            surfaceMapperGui.addSketch(conductableSketch);
        }
        //surfaceMapperGui.setBackgroundImage(image);
        println("IP ADDRESS: " + ipAddress);
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ipAddress, 5204), 5000);
            client = new Client(this, socket);
        } catch (IOException e) {
            println("error connecting to socket at " + ipAddress);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            println("Please provide an ip address");
        }
        ipAddress = args[0];
        //PApplet.main(new String[]{"--present", "--display=1", MainConductorSketch.class.getName() });
        PApplet.main(new String[]{"--display=1", MainConductorSketch.class.getName() });
    }

    public void clientEvent(Client theClient) {
        println("inside clientEvent");
        String data = theClient.readStringUntil('\n');
        if (null == data) {
            return;
        }
        print("Data received: " + data);
        String[] values = data.split(",");
        PVector velocity = new PVector(Float.valueOf(values[0]), Float.valueOf(values[1]));
        for (Sketch sketch : surfaceMapperGui.getSketchList()) {
            if (sketch instanceof BouncyBallSketch) {
                BouncyBallSketch bouncyBallSketch = (BouncyBallSketch) sketch;
                bouncyBallSketch.updateBallVectors(velocity);
            }
        }
    }

}
