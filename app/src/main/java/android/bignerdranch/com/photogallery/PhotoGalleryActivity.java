package android.bignerdranch.com.photogallery;

import androidx.fragment.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return super.getLayoutResId();
    }

}