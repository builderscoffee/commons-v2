package eu.builderscoffee.commons.common.utils;

import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import lombok.val;

import java.sql.Timestamp;
import java.util.Date;

public class ProfilCache extends Cache<String, ProfilEntity> {

    public ProfilEntity getOrCreate(String uniqueId) {
        val cached = CommonsBukkit.getInstance().getProfilCache().get(uniqueId);
        if (cached == null) {
            val profil = new ProfilEntity();
            profil.setUniqueId(uniqueId);
            profil.setLang(Profil.Languages.FR);
            profil.setUpdateDate(new Timestamp(new Date().getTime()));
            profil.setCreationDate(new Timestamp(new Date().getTime()));
            return profil;
        }
        return cached;
    }
}
