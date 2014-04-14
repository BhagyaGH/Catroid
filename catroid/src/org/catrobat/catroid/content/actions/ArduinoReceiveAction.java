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
package org.catrobat.catroid.content.actions;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Adrian Schnedlitz
 * 
 */
public class ArduinoReceiveAction extends TemporalAction {

	private int pinNumber;
	private Boolean pinValue;
	private static BluetoothSocket bluetoothSocket = null;
	private static BluetoothSocket tmpSocket = null;
	public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static BluetoothDevice bluetoothDevice = null;

	private static InputStream bluetoothInputStream = null;
	private static String bluetoothMacAdress = "";

	public int getPinNumber() {
		return pinNumber;
	}

	public boolean getPinValue() {
		return pinValue;
	}

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public static void setBluetoothMacAdress(String newMacAdress) {
		bluetoothMacAdress = newMacAdress;
	}

	public static String getBluetoothMacAdress() {
		return bluetoothMacAdress;
	}

	public static void setBluetoothSocket(BluetoothSocket newBluetoothSocket) {
		bluetoothSocket = newBluetoothSocket;
	}

	public static int receiveDataViaBluetoothSocket(BluetoothSocket inputBluetoothSocket, char pinValue,
			char pinNumberLowerByte, char pinNumberHigherByte) {

		OutputStream bluetoothOutputStream = null;

		try {
			inputBluetoothSocket.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bluetoothOutputStream = inputBluetoothSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			bluetoothOutputStream.write(pinNumberLowerByte);
			bluetoothOutputStream.write(pinNumberHigherByte);
			bluetoothOutputStream.write(pinValue);
			bluetoothOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			bluetoothInputStream = inputBluetoothSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int inputMessage = 0;
		try {
			inputMessage = bluetoothInputStream.read(); //we must read more then 1 byte
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputMessage;
	}

	public static void initBluetoothConnection(String MacAdress) {

		bluetoothDevice = bluetoothAdapter.getRemoteDevice(MacAdress);

		try {
			tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		bluetoothSocket = tmpSocket;
		bluetoothAdapter.cancelDiscovery();
	}

	@Override
	protected void update(float percent) {

		//		Log.d("Arduino", "BT Message " + pinNumberLowerByte + "" + pinNumberHigherByte + "" + pinValue
		//				+ "---------------");
		//
		//		ArduinoSendAction.receiveDataViaBluetoothSocket(bluetoothSocket, pinValue, pinNumberLowerByte,
		//				pinNumberHigherByte);
	}
}