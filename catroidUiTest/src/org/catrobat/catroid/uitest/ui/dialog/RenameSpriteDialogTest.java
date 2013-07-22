/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui.dialog;

import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity_Shruti;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class RenameSpriteDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity_Shruti> {

	private String testProject = UiTestUtils.PROJECTNAME1;
	private String cat = "cat";
	private String kat = "kat";
	private String catMixedCase = "CaT";

	public RenameSpriteDialogTest() {
		super(MainMenuActivity_Shruti.class);
	}

	@Override
	public void tearDown() throws Exception {
		// normally super.teardown should be called last
		// but tests crashed with Nullpointer
		super.tearDown();
		ProjectManager.getInstance().deleteCurrentProject();
	}

	public void testRenameSpriteDialog() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.sleep(1000);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.clickLongOnText(cat);

		solo.clickOnText(solo.getString(R.string.rename));
		solo.sleep(100);
		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, kat);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		String first = ((Sprite) spritesList.getItemAtPosition(1)).getName();

		assertEquals("The first sprite is NOT rename!", first, kat);
	}

	public void testRenameSpriteDialogMixedCase() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.sleep(500);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.clickLongOnText(cat);

		solo.sleep(1000);
		solo.clickOnText(solo.getString(R.string.rename));
		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, catMixedCase);
		solo.sendKey(Solo.ENTER);

		ListView spriteList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		String first = ((Sprite) spriteList.getItemAtPosition(1)).getName();
		assertEquals("The first sprite name was not renamed to Mixed Case", first, catMixedCase);
	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
	}
}
