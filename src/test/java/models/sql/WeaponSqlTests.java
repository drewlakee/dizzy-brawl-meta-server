package models.sql;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteWeapon;
import dizzybrawl.database.models.Weapon;
import io.vertx.pgclient.impl.RowImpl;
import io.vertx.sqlclient.impl.RowDesc;
import org.junit.Test;

import java.util.List;

public class WeaponSqlTests {

    @Test
    public void convertEmptySqlRowToWeapon() {
        List<String> weaponColumns =
                List.of(Weapon.WEAPON_ID, Weapon.WEAPON_COST, Weapon.WEAPON_NAME);
        RowDesc rowDesc = new RowDesc(weaponColumns);
        RowImpl row = new RowImpl(rowDesc);

        Weapon weapon = new Weapon(row);

        assert weapon.isEmpty();
    }

    @Test
    public void convertEmptySqlRowToConcreteWeapon() {
        List<String> weaponColumns =
                List.of(ConcreteWeapon.WEAPON_ID, ConcreteWeapon.WEAPON_COST, ConcreteWeapon.WEAPON_NAME,
                        ConcreteWeapon.WEAPON_LEVEL, ConcreteWeapon.WEAPON_IS_ENABLED, Character.CHARACTER_ID);
        RowDesc rowDesc = new RowDesc(weaponColumns);
        RowImpl row = new RowImpl(rowDesc);

        ConcreteWeapon weapon = new ConcreteWeapon(row);

        assert weapon.isEmpty();
    }
}
