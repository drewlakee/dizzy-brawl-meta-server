package models.json;

import com.google.common.collect.ComparisonChain;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterType;
import dizzybrawl.database.models.ConcreteWeapon;
import dizzybrawl.database.models.Weapon;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

public class WeaponJsonTests {

    @Test
    public void convertWeaponToJson() {
        Weapon weapon = new Weapon();
        weapon.setId(1L);
        weapon.setName("COLD BLAZER");
        weapon.setCost(100);
        CharacterType characterType = CharacterType.createEmpty();
        characterType.setId(1);
        weapon.setCharacterType(characterType);

        JsonObject weaponJson = weapon.toJson();

        assert weaponJson != null;
        assert !weaponJson.isEmpty();
        assert ComparisonChain.start()
                .compare(weapon.getId(), weaponJson.getLong(Weapon.WEAPON_ID))
                .compare(weapon.getName(), weaponJson.getString(Weapon.WEAPON_NAME))
                .compare(weapon.getCost(), (int) weaponJson.getInteger(Weapon.WEAPON_COST))
                .compare(weapon.getCharacterType().getId(), (int) weaponJson.getInteger(CharacterType.CHARACTER_TYPE_ID))
                .result() == 0;
    }

    @Test
    public void convertConcreteWeaponToJson() {
        ConcreteWeapon weapon = new ConcreteWeapon();
        weapon.setId(1L);
        weapon.setName("COLD BLAZER");
        weapon.setCost(100);
        CharacterType characterType = CharacterType.createEmpty();
        characterType.setId(1);
        weapon.setCharacterType(characterType);
        weapon.setCharacterID(1L);
        weapon.setLevel(1);
        weapon.setEnabled(true);

        JsonObject weaponJson = weapon.toJson();

        assert weaponJson != null;
        assert !weaponJson.isEmpty();
        assert ComparisonChain.start()
                .compare(weapon.getId(), weaponJson.getLong(Weapon.WEAPON_ID))
                .compare(weapon.getName(), weaponJson.getString(Weapon.WEAPON_NAME))
                .compare(weapon.getCost(), (int) weaponJson.getInteger(Weapon.WEAPON_COST))
                .compare(weapon.getCharacterType().getId(), (int) weaponJson.getInteger(CharacterType.CHARACTER_TYPE_ID))
                .compare(weapon.getCharacterID(), weaponJson.getLong(Character.CHARACTER_ID))
                .compare(weapon.getLevel(), (int) weaponJson.getInteger(ConcreteWeapon.WEAPON_LEVEL))
                .compareTrueFirst(weapon.isEnabled(), weaponJson.getBoolean(ConcreteWeapon.WEAPON_IS_ENABLED))
                .result() == 0;
    }
}
