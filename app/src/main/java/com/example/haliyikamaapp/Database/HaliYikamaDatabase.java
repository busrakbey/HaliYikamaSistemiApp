package com.example.haliyikamaapp.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.haliyikamaapp.Model.Dao.AuthTokenDao;
import com.example.haliyikamaapp.Model.Dao.BolgeDao;
import com.example.haliyikamaapp.Model.Dao.GorevFormBilgileriDao;
import com.example.haliyikamaapp.Model.Dao.GorevlerDao;
import com.example.haliyikamaapp.Model.Dao.HesapDao;
import com.example.haliyikamaapp.Model.Dao.KaynakDao;
import com.example.haliyikamaapp.Model.Dao.MusteriDao;
import com.example.haliyikamaapp.Model.Dao.MusteriIletisimDao;
import com.example.haliyikamaapp.Model.Dao.MusteriTuruDao;
import com.example.haliyikamaapp.Model.Dao.OlcuBirimDao;
import com.example.haliyikamaapp.Model.Dao.PermissionsDao;
import com.example.haliyikamaapp.Model.Dao.SIlDao;
import com.example.haliyikamaapp.Model.Dao.SIlceDao;
import com.example.haliyikamaapp.Model.Dao.STenantDao;
import com.example.haliyikamaapp.Model.Dao.SUserDao;
import com.example.haliyikamaapp.Model.Dao.SiparisDao;
import com.example.haliyikamaapp.Model.Dao.SiparisDetayDao;
import com.example.haliyikamaapp.Model.Dao.SmsDao;
import com.example.haliyikamaapp.Model.Dao.SubeDao;
import com.example.haliyikamaapp.Model.Dao.UrunDao;
import com.example.haliyikamaapp.Model.Dao.UrunFiyatDao;
import com.example.haliyikamaapp.Model.Dao.UrunSubeDao;
import com.example.haliyikamaapp.Model.Dao.UserDao;
import com.example.haliyikamaapp.Model.Dao.UserPermissionsDao;
import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.Permissions;
import com.example.haliyikamaapp.Model.Entity.STenant;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.S_User;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sms;
import com.example.haliyikamaapp.Model.Entity.SubPermissions;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.Model.Entity.User;
import com.example.haliyikamaapp.Model.Entity.UserPermissions;


@Database(entities = {Musteri.class, Siparis.class, SiparisDetay.class, Urun.class,
        UrunSube.class, UrunFiyat.class, STenant.class,
        MusteriIletisim.class, OlcuBirim.class, AuthToken.class,
        Sube.class, MusteriTuru.class,
        Gorevler.class, GorevFomBilgileri.class,
        S_IL.class, S_ILCE.class, Bolge.class, User.class,
        Permissions.class, SubPermissions.class, S_User.class,
        UserPermissions.class, Sms.class, Hesap.class, Kaynak.class} ,
        version = 1, exportSchema = false)
public abstract class HaliYikamaDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "SATIS_OTOMASYON_DB";


    private static HaliYikamaDatabase instance;

    public abstract MusteriDao musteriDao();
    public abstract MusteriIletisimDao musteriIletisimDao();
    public abstract SiparisDao siparisDao();
    public abstract SiparisDetayDao siparisDetayDao();
    public abstract OlcuBirimDao olcuBirimDao();
    public abstract STenantDao sTenantDao();
    public abstract UrunDao urunDao();
    public abstract UrunFiyatDao urunFiyatDao();
    public abstract UrunSubeDao urunSubeDao();
    public abstract AuthTokenDao authToken();
    public abstract SubeDao subeDao();
    public abstract MusteriTuruDao musteriTuruDao();
    public abstract GorevlerDao gorevlerDao();
    public abstract SIlDao sIlDao();
    public abstract SIlceDao sIlceDao();
    public abstract BolgeDao bolgeDao();
    public abstract UserDao userDao();
    public abstract PermissionsDao permissionsDao();
    public  abstract GorevFormBilgileriDao gorevFomBilgileriDao();
    public  abstract SUserDao sUserDao();
    public  abstract UserPermissionsDao userPermissionsDao();
    public  abstract SmsDao smsDao();
    public  abstract HesapDao hesapDao();
    public  abstract KaynakDao kaynakDao();



    public static synchronized HaliYikamaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), HaliYikamaDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE)
                   // .addCallback(roomCallBack)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }

        return instance;
    }

    @VisibleForTesting
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
          /*  database.execSQL(
                    "ALTER TABLE AUTH_TOKEN ADD COLUMN tag TEXT DEFAULT ''");*/
        }
    };








    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//            new PopulateDbAsyncTask(instance).execute();
        }
    };


}
