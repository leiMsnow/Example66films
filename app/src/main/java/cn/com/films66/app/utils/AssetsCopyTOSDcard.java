package cn.com.films66.app.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsCopyTOSDcard {
    Context mContext;

    public AssetsCopyTOSDcard(Context context) {
        super();
        this.mContext = context;
    }

    /**
     * @param assetPath asset下的路径
     * @param SDPath    SDpath下保存路径
     */
    public void assetToSD(String assetPath, String SDPath) {

        AssetManager asset = mContext.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames;
        FileOutputStream out = null;
        InputStream in = null;
        try {
            filenames = asset.list(assetPath);
            if (filenames.length > 0) {//说明是目录
                //创建目录
                getDirectory(assetPath);

                for (String fileName : filenames) {
                    assetToSD(assetPath + "/" + fileName, SDPath + "/" + fileName);
                }
            } else {//说明是文件，直接复制到SD卡
                File file = new File(SDPath);
                String path = assetPath.substring(0, assetPath.lastIndexOf("/"));
                getDirectory(path);

                if (!file.exists()) {
                    file.createNewFile();
                }
                //将内容写入到文件中
                in = asset.open(assetPath);
                out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = in.read(buffer)) != -1) {
                    out.write(buffer, 0, byteCount);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //分级建立文件夹
    private void getDirectory(String path) {
        //对SDPath进行处理，分层级建立文件夹
        String[] s = path.split("/");
        String str = Environment.getExternalStorageDirectory().toString();
        for (String value : s) {
            str = str + "/" + value;
            File file = new File(str);
            if (!file.exists()) {
                file.mkdir();
            }
        }

    }
}
