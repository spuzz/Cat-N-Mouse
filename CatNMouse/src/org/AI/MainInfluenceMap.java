/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.AI;

import java.util.ArrayList;
import org.GameObjects.MapObj;
import org.GameObjects.Sprite;

/**
 *
 * @author Spuz
 *
 * This is a sub class of influence map for the AI, if one point is visible it gives the
 * whole grid of information to the ai and this information stays until the sprite is seen
 * again so that the AI can use it to base its movements on an estimation of where the enemy is
 * even if he isnt visible anymore
 */
public class MainInfluenceMap extends InfluenceMap {
    private ArrayList<Sprite> oldMaps;
    private ArrayList<Sprite> enemies;

    public <T> MainInfluenceMap(ArrayList<Sprite> sprites,MapObj map,ArrayList<Sprite> opponents) {
        super(sprites,map);
        Boolean[] visableSprites = new Boolean[sprites.size()];
        oldMaps = new ArrayList<Sprite>();
        this.enemies = opponents;
        for(int a=0;a<sprites.size();a++) {
            visableSprites[a] = false;
            oldMaps.add(map.getFakeSprite().copy());
            oldMaps.get(a).setInfluence(sprites.get(a).getInfluence());
            oldMaps.get(a).setInfluenceMap(this.copy());
            oldMaps.get(a).getInfluenceMap().resetMap();
        }

    }

    /* Update is similar to standard influence map except it places the whole grid over the top
    * even if only 1 point is visible, keeps track of old influences and only updates them
    * when the enemy is seen again
    */
    public void update() {
        try {
            ArrayList<Sprite> tmpSprites = new ArrayList<Sprite>();
            ArrayList<Sprite> oldSprites = new ArrayList<Sprite>();
            for(int a=0;a<sprites.size();a++) {
                oldSprites.add(sprites.get(a));
            }
            for(int a=0;a<sprites.size();a++) {
                sprites.get(a).getInfluenceMap().update();
            }
            boolean anyVisable = false;
            for(int a=0;a<sprites.size();a++) {
                boolean vis = false;
                for(int b=0;b<enemies.get(0).getVisionGrid().getVisionPoints().size();b++) {
                    if(sprites.get(a).getInfluenceMap().getTileInfluence(enemies.get(0).getVisionGrid().getVisionPoints().get(b),false) > 0) {
                        tmpSprites.add(sprites.get(a));
                        oldMaps.get(a).setPosition(sprites.get(a).position());
                        oldMaps.get(a).setInfluenceMap(sprites.get(a).getInfluenceMap().copy());
                        vis = true;
                        anyVisable = true;
                        break;
                    }
                }
                if(vis == false) {
                    tmpSprites.add(oldMaps.get(a));
                }
            }
            for(int a=0;a<enemies.size();a++) {
                enemies.get(a).setEnemyDetected(anyVisable);
            }
            
            this.sprites = tmpSprites;
            super.update();
            this.sprites = oldSprites;
        }
        catch(NullPointerException e) {
            
        }
    }
}
