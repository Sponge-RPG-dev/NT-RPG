package cz.neumimto.rpg.common.model;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.Date;

public class DateKeyPair {

    @Path("dateReceived")
    private Date dateReceived;

    @Path("sourceKey")
    private String sourceKey;

    public DateKeyPair(String sourceKey) {
        this(new Date(), sourceKey);
    }

    public DateKeyPair(Date dateReceived, String sourceKey) {
        this.dateReceived = dateReceived;
        this.sourceKey = sourceKey;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public String getSourceKey() {
        return sourceKey;
    }

}
