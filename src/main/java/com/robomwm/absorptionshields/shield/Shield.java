package com.robomwm.absorptionshields.shield;

public class Shield
{
    private String name;
    private String formattedName;
    private float maxShieldStrength;
    private long regenTime;
    private float regenRate;

    private long regenCounter; //when equal to regenTime, shield regen delay has elapsed

    /**
     * @param strength maximum healthpoints of shield
     * @param time Time before regenerating in ticks
     * @param rate How many healthpoints to regenerate per second
     */
    public Shield(String name, String formattedName, float strength, long time, float rate)
    {
        this.name = name;
        this.formattedName = formattedName;
        this.maxShieldStrength = strength;
        this.regenTime = time;
        this.regenRate = rate;

        this.regenCounter = time;
    }

    /**
     * Copy constructor
     * @param template
     */
    public Shield(Shield template)
    {
        this.name = template.name;
        this.formattedName = template.formattedName;
        this.maxShieldStrength = template.maxShieldStrength;
        this.regenTime = template.regenTime;
        this.regenRate = template.regenRate;

        this.regenCounter = regenTime;
    }

    public void resetRegenCounter()
    {
        regenCounter = 0L;
    }

    /**
     *
     * @param ticks
     * @return if shield is allowed to regenerate
     */
    public boolean incrementCounter(long ticks)
    {
        if (regenCounter < regenTime)
            regenCounter += ticks;
        return regenCounter >= regenTime;
    }

    public float getMaxShieldStrength()
    {
        return maxShieldStrength;
    }

    public float getRegenRate()
    {
        return regenRate;
    }

    public long getRegenTime()
    {
        return regenTime;
    }

    public String getName()
    {
        return name;
    }

    public String getFormattedName()
    {
        return formattedName;
    }
}
