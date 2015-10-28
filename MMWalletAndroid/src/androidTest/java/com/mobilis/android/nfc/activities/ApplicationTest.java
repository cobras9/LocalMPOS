/*
package com.mobilis.android.nfc.activities;

import android.app.Activity;
import android.widget.Button;

import com.mobilis.android.nfc.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

*/
/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 *//*


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class ApplicationTest extends AndroidTestCase{

    @Test
    public void testLogin() {
        Activity activity = Robolectric.buildActivity(LoginActivity.class).create().get();
        assertTrue(activity != null);
        Button changeId = (Button)activity.findViewById(R.id.Activity_Login_Button_PhoneNumber);
        assertEquals(changeId.getText(), "CHANGE ID");
*/
/*        LoginActivity activity = Robolectric.setupActivity(LoginActivity.class);

       */
/* assertThat(changeId.getText(),equalTo("Change Id"));
       // .performClick();

        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity()).isEqualTo(expectedIntent);
*//*

    }
}

*/
