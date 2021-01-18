package com.example.haliyikamaapp.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.haliyikamaapp.Model.Dao.AuthTokenDao;
import com.example.haliyikamaapp.Model.Dao.GorevFormBilgileriDao;
import com.example.haliyikamaapp.Model.Dao.GorevlerDao;
import com.example.haliyikamaapp.Model.Dao.MusteriDao;
import com.example.haliyikamaapp.Model.Dao.MusteriIletisimDao;
import com.example.haliyikamaapp.Model.Dao.MusteriTuruDao;
import com.example.haliyikamaapp.Model.Dao.OlcuBirimDao;
import com.example.haliyikamaapp.Model.Dao.STenantDao;
import com.example.haliyikamaapp.Model.Dao.SiparisDao;
import com.example.haliyikamaapp.Model.Dao.SiparisDetayDao;
import com.example.haliyikamaapp.Model.Dao.SubeDao;
import com.example.haliyikamaapp.Model.Dao.UrunDao;
import com.example.haliyikamaapp.Model.Dao.UrunFiyatDao;
import com.example.haliyikamaapp.Model.Dao.UrunSubeDao;
import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.STenant;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;


@Database(entities = {Musteri.class, Siparis.class, SiparisDetay.class, Urun.class, UrunSube.class, UrunFiyat.class, STenant.class,
        MusteriIletisim.class, OlcuBirim.class, AuthToken.class, Sube.class, MusteriTuru.class, Gorevler.class, GorevFomBilgileri.class},
        version = 8, exportSchema = false)
public abstract class HaliYikamaDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "HALIYIKAMA_DB";
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
    public  abstract GorevFormBilgileriDao gorevFomBilgileriDao();


    public static synchronized HaliYikamaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), HaliYikamaDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(roomCallBack)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//            new PopulateDbAsyncTask(instance).execute();
        }
    };


}
