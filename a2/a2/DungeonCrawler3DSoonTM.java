package a2;

import a2.newdc.GhostAvatar;
import a2.newdc.assets.AssetInfo;
import a2.newdc.assets.PlayableAsset;
import a2.newdc.assets.TileAsset;
import graphicslib3D.Quaternion;
import sage.app.BaseGame;

import sage.display.*;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.bounding.BoundingBox;
import sage.scene.shape.*;
import sage.scene.Group;
import sage.scene.HUDString;

import sage.input.*;
import sage.input.action.*;
import sage.renderer.IRenderer;
import sage.camera.*;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import myGameEngine.Camera3PController;
import myGameEngine.MoveBackAction;
import myGameEngine.MoveForwardAction;
import myGameEngine.MoveLeftAction;
import myGameEngine.MoveRightAction;
import myGameEngine.MoveUpAction;
import myGameEngine.MyDisplaySystem;
import myGameEngine.PitchDownAction;
import myGameEngine.PitchUpAction;
import myGameEngine.QuitGameAction;
import myGameEngine.YawLeftAction;
import myGameEngine.YawRightAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.text.DecimalFormat;

import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.TerrainBlock;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class DungeonCrawler3DSoonTM extends BaseGame //implements MouseListener
{
    private int scorePOne = 0;
    private int scorePTwo = 0;
    private float time;
    private HUDString player1ID;

    private HUDString timeStringPOne;

    private int numCrashes = 0;
    private int numStandardShapes = 10;
    private Random random;

    private MyPyramid avatarOne;

    private PyramidGroup pyramids;
    private RotationController rotational;


    private String kbName, gpName;

    private int maxXDistanceAbsolute = 10;
    private int maxYDistanceAbsolute = 10;
    private int maxZDistanceAbsolute = 10;

    // ### Zag
    private AssetInfo assetInfo;
    // ### Zag

    // ### TJ
    private SkyBox skyBox;
    private TerrainBlock theTerrain;
    private Group background;

    // use File.separator later
    private String zagDrivePath = "C:\\Users\\Zagak\\Google Drive\\CSC165\\DungeonCrawler\\assets\\";
    private String zuluDrivePath = "your path here";
    private String tjDrivePath = "your path here";
    private String googleDrivePath = zagDrivePath;

    // http://www.farmpeeps.com/fp_skyboxes.html
    private String terrainTexture = googleDrivePath + "Skyboxs\\0\\down.jpg";
    private String skyboxDown = googleDrivePath + "Skyboxs\\0\\down.jpg";
    private String skyboxEast = googleDrivePath + "Skyboxs\\0\\east.jpg";
    private String skyboxNorth = googleDrivePath + "Skyboxs\\0\\north.jpg";
    private String skyboxSouth = googleDrivePath + "Skyboxs\\0\\south.jpg";
    private String skyboxWest = googleDrivePath + "Skyboxs\\0\\west.jpg";
    private String skyboxUp = googleDrivePath + "Skyboxs\\0\\up.jpg";
    // TODO move skybox into assetInfo

    private boolean connected;
    // ### TJ

    //private IDisplaySystem display;
    private IDisplaySystem display;
    private IRenderer renderer;
    private ICamera cameraOne;
    private IInputManager im;
    private IEventManager eventManager;

    private Camera3PController camOne;

    protected void initGame()
    {
        //test f = new test();
        //f.listControllers();

        //display = getDisplaySystem();
        display.setTitle("Dog Catcher!");

        renderer = display.getRenderer();

        assetInfo = new AssetInfo(googleDrivePath);

        createPlayers();
        createPlayerHUDs();
        initEnvironment();
        initGameObjects();
        associateDefaultKeyAndControllerBindings();

        camOne = new Camera3PController(cameraOne, avatarOne, im, kbName, "K");

        super.update((float) 0.0);
    }

    protected void initSystem()
    {
        //call a local method to create a DisplaySystem object
        display = createDisplaySystem();
        setDisplaySystem(display);
        //create an Input Manager
        im = new InputManager();
        setInputManager(im);
        //create an (empty) gameworld
        ArrayList<SceneNode> gameWorld = new ArrayList<>();
        setGameWorld(gameWorld);
        //super.initSystem();
    }

    private IDisplaySystem createDisplaySystem()
    {
        IDisplaySystem display = new MyDisplaySystem(1280, 720, 24, 20, false, "sage.renderer.jogl.JOGLRenderer");
        System.out.print("\nWaiting for display creation...");
        int count = 0;
        // wait until display creation completes or a timeout occurs
        while (!display.isCreated())
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException("Display creation interrupted");
            }

            count++;
            System.out.print("+");

            if (count % 80 == 0)
            {
                System.out.println();
            }
            if (count > 2000) // 20 seconds (approx.)
            {
                throw new RuntimeException("Unable to create display");
            }
        }
        System.out.println();
        return display;
    }


    private void createPlayers()
    {
        SceneNode wizard = assetInfo.playables.get("wizard")
                .make(new Point3D(0, .3, 0), new Point3D(.01, .01, .01), new Quaternion(1, new double[]{0, 0, 0}));
        addGameWorldObject(wizard);

        TileAsset tile =assetInfo.tiles.get("Tile");
        addGameWorldObject(tile.make(new Point3D(0,0,0),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(0,0,2),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(0,0,4),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(0,0,6),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(2,0,0),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(4,0,0),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(6,0,0),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));
        addGameWorldObject(tile.make(new Point3D(6,0,2),new Point3D(.95,.95,.95),new Quaternion(1, new double[] {0,0,0})));

        avatarOne = new MyPyramid("PLAYER1");
        avatarOne.translate(0, 1, 50);
        avatarOne.rotate(180, new Vector3D(0, 1, 0));
        addGameWorldObject(avatarOne);
        cameraOne = new JOGLCamera(renderer);
        cameraOne.setPerspectiveFrustum(60, 2, 1, 1000);
        cameraOne.setViewport(0.0, 1.0, 0.0, 1);
        cameraOne.setLocation(new Point3D(0, 0, 0));
    }

    private void createPlayerHUDs()
    {
        timeStringPOne = new HUDString("Time = ");
        timeStringPOne.setLocation(0, 0.05);
        cameraOne.addToHUD(timeStringPOne);

        player1ID = new HUDString("Player 1: ");// + scorePOne);
        player1ID.setName("Player1ID");
        player1ID.setLocation(0, 0.10);
        player1ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
        player1ID.setColor(Color.red);
        player1ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
        cameraOne.addToHUD(player1ID);
    }

    private void initEnvironment()
    {
        eventManager = EventManager.getInstance();
        //display.addMouseListener(this);
        random = new Random();
    }

    private void initGameObjects()
    {
        // ### TJ skybox
        skyBox = new SkyBox("skybox", 200, 200, 200);
        skyBox.setTexture(SkyBox.Face.Up,  TextureManager.loadTexture2D(skyboxUp));
        skyBox.setTexture(SkyBox.Face.Down, TextureManager.loadTexture2D(skyboxDown));
        skyBox.setTexture(SkyBox.Face.North, TextureManager.loadTexture2D(skyboxNorth));
        skyBox.setTexture(SkyBox.Face.South, TextureManager.loadTexture2D(skyboxSouth));
        skyBox.setTexture(SkyBox.Face.East, TextureManager.loadTexture2D(skyboxEast));
        skyBox.setTexture(SkyBox.Face.West, TextureManager.loadTexture2D(skyboxWest));
        skyBox.setZBufferStateEnabled(false);
        addGameWorldObject(skyBox);

        theTerrain = initTerrain();
        theTerrain.scale(2, 1, 2);
        background = new Group();
        background.addChild(theTerrain);

        addGameWorldObject(background);
        /// ### TJ end skybox

        Dog dg1 = new Dog();
        Matrix3D dg1M = dg1.getLocalTranslation();
        dg1M.translate(10, 0, 10);
        dg1.setLocalTranslation(dg1M);
        Point3D center = new Point3D(0, -0.5, 0.5);
        BoundingBox bb = new BoundingBox(center, (float) 4.0, (float) 7.25, (float) 9.0);
        dg1.setLocalBound(bb);

        Matrix3D scale = dg1.getLocalScale();
        int rand = random.nextInt(5);
        rand += 3;
        double scaleFactor = (double) rand / 10;
        //double scaleFactor = (random.nextInt(5) + 1) / 10;
        scale.scale(scaleFactor, scaleFactor, scaleFactor);
        //dg1.setLocalScale(scale);
        //dg1.setShowBound(true);

        addGameWorldObject(dg1);
        dg1.updateWorldBound();

        initWorldAxes();
    }

    // ### TJ terrain
    private static TerrainBlock createTerBlock(AbstractHeightMap heightMap)
    {
        float heightScale = 0.05f;
        Vector3D terrainScale = new Vector3D(1, heightScale, 1);

        // use the size of the height map as the size of the terrain
        int terrainSize = heightMap.getSize();

        // specify terrain origin so heightmap (0,0) is at world origin
        float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
        Point3D terrainOrigin = new Point3D(-64.5, -cornerHeight-2, -64.5);

        // create a terrain block using the height map
        String name = "Terrain:" + heightMap.getClass().getSimpleName();

        return new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
    }

    private TerrainBlock initTerrain()
    {
        // create height map and terrain block
        HillHeightMap myHillHeightMap = new HillHeightMap(129, 2000, 5.0f, 20.0f, (byte) 2, 12345);
        myHillHeightMap.setHeightScale(0.1f);
        TerrainBlock hillTerrain = createTerBlock(myHillHeightMap);

        // create texture and texture state to color the terrain
        Texture groundTexture = TextureManager.loadTexture2D(terrainTexture);
        groundTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);

        TextureState groundState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
        groundState.setTexture(groundTexture, 0);
        groundState.setEnabled(true);

        // apply the texture to the terrain
        hillTerrain.setRenderState(groundState);
        return hillTerrain;
    }
    // ### TJ end terrain



    private void initWorldAxes()
    {
        Point3D origin = new Point3D(0, 0, 0);
        Point3D xEnd = new Point3D(100, 0, 0);
        Point3D yEnd = new Point3D(0, 100, 0);
        Point3D zEnd = new Point3D(0, 0, 100);
        Line xAxis = new Line(origin, xEnd, Color.red, 2);
        Line yAxis = new Line(origin, yEnd, Color.green, 2);
        Line zAxis = new Line(origin, zEnd, Color.blue, 2);
        addGameWorldObject(xAxis);
        addGameWorldObject(yAxis);
        addGameWorldObject(zAxis);
    }

    private void associateDefaultKeyAndControllerBindings()
    {
        im = getInputManager();
        gpName = im.getFirstGamepadName();

        //Controller c = new Controller();
        if (gpName == null && im.getControllers().size() > 2)
        {
            gpName = im.getControllers().get(2).getName();
            //gpName = im.getGamepadController(2).getName();
        }

        kbName = im.getKeyboardName();

        initPlayerOneControls();
    }


    private void initPlayerOneControls()
    {
        QuitGameAction quitActionPOne = new QuitGameAction(this);

        MoveForwardAction mvFwdActionPOne = new MoveForwardAction(cameraOne, (float) 0.1);
        MoveLeftAction mvLeftActionPOne = new MoveLeftAction(cameraOne, (float) 0.1);
        MoveRightAction mvRightActionPOne = new MoveRightAction(cameraOne, (float) 0.1);
        MoveBackAction mvBackActionPOne = new MoveBackAction(cameraOne, (float) 0.1);
        MoveUpAction mvUpActionPOne = new MoveUpAction(cameraOne, (float) 0.1);
        //MoveDownAction mvDownActionPOne = new MoveDownAction(cameraOne, (float) 0.1);

        PitchUpAction pitchUpActionPOne = new PitchUpAction(cameraOne, (float) 0.1);
        PitchDownAction pitchDownActionPOne = new PitchDownAction(cameraOne, (float) 0.1);
        RollRightAction rollRightActionPOne = new RollRightAction(cameraOne, (float) 0.1);
        RollLeftAction rollLeftActionPOne = new RollLeftAction(cameraOne, (float) 0.1);
        YawRightAction yawRightActionPOne = new YawRightAction(cameraOne, (float) 0.1);
        YawLeftAction yawLeftActionPOne = new YawLeftAction(cameraOne, (float) 0.1);


        mvFwdActionPOne.setAvatar(avatarOne);
        mvBackActionPOne.setAvatar(avatarOne);
        mvLeftActionPOne.setAvatar(avatarOne);
        mvRightActionPOne.setAvatar(avatarOne);

        yawLeftActionPOne.setAvatar(avatarOne);
        yawRightActionPOne.setAvatar(avatarOne);

        //TODO: Switch all of the actions over to the correct objects


        //QUIT
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.ESCAPE,
                           quitActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

        //MOVE FORWARD
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.W,
                           mvFwdActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.W,
                           mvFwdActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //MOVE BACKWARD
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.S,
                           mvBackActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.S,
                           mvBackActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //NOTE: This is attached to mvFwdAction.
        //The Fwd/Backward need to the on the same object when attached to an AXIS
        //The button ones should be too, but I'm not going worry about that yet

        //MOVE LEFT
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.A,
                           mvLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.A,
                           mvLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);


        //MOVE RIGHT
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.D,
                           mvRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.D,
                           mvRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //MOVE UP
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.SPACE,
                           mvUpActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.SPACE,
                           mvUpActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //		//MOVE DOWN
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.F,
        //				mvDownAction,
        //				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.F,
        //				mvDownAction,
        //				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);


        //TODO: Note - Roll should be mapped to Left/Right and Yaw should be Q/E to match cockpit controls

        //YAW
        //ROTATE LEFT
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.Q,
                           yawLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.Q,
                           yawLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //ROTATE RIGHT
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.E,
                           yawRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.E,
                           yawRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //PITCH -- TODO: Pitch commands should be switched to match actual cockpit controls
        //ROTATE UP
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.UP,
                           pitchUpActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.UP,
                           pitchUpActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //ROTATE DOWN
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.DOWN,
                           pitchDownActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.DOWN,
                           pitchDownActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //ROLL
        //ROTATE COUNTERCLOCKWISE
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.LEFT,
                           rollRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.LEFT,
                           rollRightActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //ROTATE CLOCKWISE
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.RIGHT,
                           rollLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction(kbName,
                           net.java.games.input.Component.Identifier.Key.RIGHT,
                           rollLeftActionPOne,
                           IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);


        //		//ZOOM IN
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.Z,
        //				rollLeftActionPOne,
        //				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.Z,
        //				rollLeftActionPOne,
        //				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //
        //
        //		//ZOOM OUT
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.C,
        //				rollLeftActionPOne,
        //				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        //		im.associateAction(kbName,
        //				net.java.games.input.Component.Identifier.Key.C,
        //				rollLeftActionPOne,
        //				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }



    private void updateSkybox()
    {
        Point3D camLoc1 = cameraOne.getLocation();
        Matrix3D camTrans = new Matrix3D();
        camTrans.translate(camLoc1.getX(),camLoc1.getY(),camLoc1.getZ());
        skyBox.setLocalTranslation(camTrans);
        // not 2
    }


    public void update(float elapsedTimeMS)
    {
        //scoreString.setText("Score = " + score);
        time += elapsedTimeMS;
        DecimalFormat df = new DecimalFormat("0.0");
        timeStringPOne.setText("Time = " + df.format(time / 1000));

        updateSkybox();
        camOne.update(elapsedTimeMS);
        super.update(elapsedTimeMS);
    }

    protected void render()
    {
        renderer.setCamera(cameraOne);
        super.render();
    }

    public void addGameWorldObject(SceneNode s)
    {
        super.addGameWorldObject(s);
    }

    // ### TJ Networking
    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public Point3D getPlayerPosition()
    {
        // TODO validate and test
        return new Point3D(avatarOne.getLocalTranslation().getCol(3));
    }

    public void startScrolling()
    {
        // TODO random stub method
    }

    public void removeGameWorldObject(GhostAvatar ghost)
    {
        // TODO list of ghostAvatars
    }

    public void textureObj(GhostAvatar ghost, String s)
    {
        // TODO
    }
    // ### TJ
}
