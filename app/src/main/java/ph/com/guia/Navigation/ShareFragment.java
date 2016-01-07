package ph.com.guia.Navigation;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ph.com.guia.R;

public class ShareFragment extends Fragment{

    EditText album_name, album_desc;
    ImageView add_photo;
    LinearLayout linearLayout;
    ArrayList<String> photos = new ArrayList<String>();
    int id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_album, container, false);

        album_name = (EditText) view.findViewById(R.id.share_album_name);
        album_desc = (EditText) view.findViewById(R.id.share_album_desc);
        add_photo = (ImageView) view.findViewById(R.id.add_photo);
        linearLayout = (LinearLayout) view.findViewById(R.id.share_photos);

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            Uri imgUri = data.getData();

            switch(requestCode) {
                case 1:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                    params.setMargins(5, 10, 5, 10);
                    ImageView iv = new ImageView(getActivity().getApplicationContext());
                    iv.setId(id++);
                    iv.setImageURI(imgUri);
                    iv.setLayoutParams(params);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setFlags(id-1);
                            startActivityForResult(intent, 2);
                        }
                    });
                    linearLayout.addView(iv);

//                    if (new ConnectionChecker(getActivity().getApplicationContext()).isConnectedToInternet()) {
//                        Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getActivity().getApplicationContext()));
//                        File file = new File(getRealPathFromURI(imgUri));
//                        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
//                        photos.add(uploadResult.get("url").toString());
//                    }
                    break;
                case 2:
                    int position = data.getFlags();
                    ((ImageView) linearLayout.findViewById(position)).setImageURI(imgUri);
                    break;
            }
        }catch (Exception e){}
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
