package jade;

import java.awt.event.KeyEvent;
import java.net.StandardSocketOptions;

public class LevelScene extends Scene{
    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelScene(){
        System.out.println("Inside Level Scene");
        Window.get().r = Window.get().g = Window.get().b = Window.get().a = 1;

    }

    @Override
    public void init(){

    }

    @Override
    public void update(float dt){
        System.out.println(1/dt+"FPS");


    }
}
