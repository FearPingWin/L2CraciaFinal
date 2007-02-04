package net.sf.l2j.gameserver.skills;

import net.sf.l2j.gameserver.model.L2Effect;

public class EffectSilencePhysical extends L2Effect {

    
    public EffectSilencePhysical(Env env, EffectTemplate template) {
        super(env, template);
    }

    public EffectType getEffectType() {
        return L2Effect.EffectType.SILENCE_PHYSICAL;
    }

    public void onStart()
    {
        getEffected().startPsychicalMuted();
    }
    
    public boolean onActionTime()
    {
        getEffected().stopPsychicalMuted(this);
        return false;
    }

    public void onExit()
    {
       getEffected().stopPsychicalMuted(this);
    }
}