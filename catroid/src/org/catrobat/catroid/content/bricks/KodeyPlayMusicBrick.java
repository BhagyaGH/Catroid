/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class KodeyPlayMusicBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String tone;
	private transient Tone toneEnum;
	private transient TextView editDuration;

	public static enum Tone {
		DO, RE, MI, FA, SO, LA, TI
	}

	public KodeyPlayMusicBrick() {
		addAllowedBrickField(BrickField.KODEY_DURATION_IN_SECONDS);
	}

	public KodeyPlayMusicBrick(Tone tone, int durationValue) {
		this.toneEnum = tone;
		this.tone = toneEnum.name();
		initializeBrickFields(new Formula(durationValue));
	}

	public KodeyPlayMusicBrick(Tone tone, Formula durationFormula) {
		this.toneEnum = tone;
		this.tone = toneEnum.name();
		initializeBrickFields(durationFormula);
	}

	protected Object readResolve() {
		if (tone != null) {
			toneEnum = Tone.valueOf(tone);
		}
		return this;
	}

	private void initializeBrickFields(Formula duration) {
		addAllowedBrickField(BrickField.KODEY_DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS, duration);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_KODEY;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_kodey_play_tone, null);
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.brick_kodey_play_tone_duration_text_view);
		textDuration.setText(String.valueOf(BrickValues.KODEY_DURATION));

		Spinner kodeyToneSpinner = (Spinner) prototypeView.findViewById(R.id.brick_kodey_select_tone_spinner);
		kodeyToneSpinner.setFocusableInTouchMode(false);
		kodeyToneSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> toneAdapter = ArrayAdapter.createFromResource(context, R.array.brick_kodey_select_tone_spinner,
				android.R.layout.simple_spinner_item);
		toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		kodeyToneSpinner.setAdapter(toneAdapter);
		kodeyToneSpinner.setSelection(toneEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new KodeyPlayMusicBrick(toneEnum, getFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS).clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_kodey_play_tone, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_kodey_play_tone_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textDuration = (TextView) view.findViewById(R.id.brick_kodey_play_tone_duration_text_view);
		editDuration = (TextView) view.findViewById(R.id.brick_kodey_play_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS).setTextFieldId(R.id.brick_kodey_play_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS).refreshTextField(view);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		ArrayAdapter<CharSequence> toneAdapter = ArrayAdapter.createFromResource(context, R.array.brick_kodey_select_tone_spinner,
				android.R.layout.simple_spinner_item);
		toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner toneSpinner = (Spinner) view.findViewById(R.id.brick_kodey_select_tone_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			toneSpinner.setClickable(true);
			toneSpinner.setEnabled(true);
		} else {
			toneSpinner.setClickable(false);
			toneSpinner.setEnabled(false);
		}

		toneSpinner.setAdapter(toneAdapter);
		toneSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				toneEnum = Tone.values()[position];
				tone = toneEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		toneSpinner.setSelection(toneEnum.ordinal());

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS));
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_kodey_play_tone_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textKodeyToneActionLabel = (TextView) view.findViewById(R.id.brick_kodey_play_tone_label);
			TextView textKodeyToneActionSelectTone = (TextView) view.findViewById(R.id.brick_kodey_select_tone_text_view);
			TextView textLKodeyToneActionDuraction = (TextView) view.findViewById(R.id.brick_kodey_play_tone_duration);
			TextView textKodeyToneActionLabelDuration = (TextView) view
					.findViewById(R.id.brick_kodey_play_tone_duration_text_view);
			TextView editDuration = (TextView) view.findViewById(R.id.brick_kodey_play_tone_duration_edit_text);

			textKodeyToneActionLabel.setTextColor(textKodeyToneActionLabel.getTextColors().withAlpha(alphaValue));
			textKodeyToneActionSelectTone.setTextColor(textKodeyToneActionSelectTone.getTextColors().withAlpha(alphaValue));
			textLKodeyToneActionDuraction.setTextColor(textLKodeyToneActionDuraction.getTextColors().withAlpha(alphaValue));
			textKodeyToneActionLabelDuration.setTextColor(textKodeyToneActionLabelDuration.getTextColors().withAlpha(
					alphaValue));
			Spinner toneSpinner = (Spinner) view.findViewById(R.id.brick_kodey_select_tone_spinner);
			ColorStateList color = textKodeyToneActionLabelDuration.getTextColors().withAlpha(alphaValue);
			toneSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editDuration.setTextColor(editDuration.getTextColors().withAlpha(alphaValue));
			editDuration.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.kodeyPlayToneAction(sprite, toneEnum,
				getFormulaWithBrickField(BrickField.KODEY_DURATION_IN_SECONDS)));
		return null;
	}

}