import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioStream;

public class Sounds {
	
    private File beamSound = new File("sounds/alienBeam.wav");
    private File bulletSound = new File("sounds/bulletSound.wav");
    private File levelUpSound = new File("sounds/levelUpSound.wav");
    private File deathSound = new File("sounds/deathSound.wav");
    private File hitmarkerSound = new File("sounds/hitmarkerSound.wav");
    private File shieldSound = new File("sounds/shieldSound.wav");
    private File bossSound = new File("sounds/bossSound.wav");
    private File bonusSound = new File("sounds/bonusSound.wav");
    private File damageSound = new File("sounds/damageSound.wav");
    private InputStream beamSoundInput;
    private InputStream bulletSoundInput;
    private InputStream levelUpSoundInput;
    private InputStream deathSoundInput;
    private InputStream hitSoundInput;
    private InputStream shieldSoundInput;
    private InputStream bossSoundInput;
    private InputStream bonusSoundInput;
    private InputStream damageSoundInput;
    
    try {
        beamSoundInput = new FileInputStream(beamSound);
        bulletSoundInput = new FileInputStream(bulletSound);
        levelUpSoundInput = new FileInputStream(levelUpSound);
        deathSoundInput = new FileInputStream(deathSound);
        hitSoundInput = new FileInputStream(hitmarkerSound);
        shieldSoundInput = new FileInputStream(shieldSound);
        bossSoundInput = new FileInputStream(bossSound);
        bonusSoundInput = new FileInputStream(bonusSound);
        damageSoundInput = new FileInputStream(damageSound);
    } catch (IOException e) {
    	
    }
}
}

