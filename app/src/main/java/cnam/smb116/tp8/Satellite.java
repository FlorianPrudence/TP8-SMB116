package cnam.smb116.tp8;

public class Satellite {
    private final float azimuth;
    private final float elevation;
    private final int svid;
    private final float cn0DbHz;
    private final boolean hasAlmanac;
    private final boolean hasEphemeris;


    public Satellite(float azimuth, float elevation, int svid, float cn0DbHz, boolean hasAlmanac, boolean hasEphemeris) {
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.svid = svid;
        this.cn0DbHz = cn0DbHz;
        this.hasAlmanac = hasAlmanac;
        this.hasEphemeris = hasEphemeris;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getElevation() {
        return elevation;
    }

    public int getSvid() {
        return svid;
    }

    public float getCn0DbHz() {
        return cn0DbHz;
    }

    public boolean isHasAlmanac() {
        return hasAlmanac;
    }

    public boolean isHasEphemeris() {
        return hasEphemeris;
    }

    // Pour l'affichage dans l'IHM des éléments de la liste
    @Override
    public String toString() {
        return "Satellite: " + svid + "; Azimuth: " + azimuth + "; Elevation: " + elevation;
    }
}
