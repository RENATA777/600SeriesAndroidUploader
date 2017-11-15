package info.nightscout.android.medtronic.message;

import android.util.Log;

import java.util.Arrays;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;

import static info.nightscout.android.utils.ToolKit.getByteIU;
import static info.nightscout.android.utils.ToolKit.getShortIU;

/**
 * Created by John on 8.11.17.
 */

public class BolusWizardTargetsResponseMessage extends MedtronicSendMessageResponseMessage {
    private static final String TAG = BolusWizardTargetsResponseMessage.class.getSimpleName();

    private byte[] targets; // [8bit] count, { [16bitBE] hi_mgdl, [16bitBE] hi_mmol, [16bitBE] lo_mgdl, [16bitBE] lo_mmol, [8bit] time period (mult 30 min) }

    protected BolusWizardTargetsResponseMessage(MedtronicCnlSession pumpSession, byte[] payload) throws EncryptionException, ChecksumException, UnexpectedMessageException {
        super(pumpSession, payload);

        if (!MedtronicSendMessageRequestMessage.MessageType.READ_BOLUS_WIZARD_BG_TARGETS.response(getShortIU(payload, 0x01))) {
            Log.e(TAG, "Invalid message received for BolusWizardTargets");
            throw new UnexpectedMessageException("Invalid message received for BolusWizardTargets");
        }

        targets = Arrays.copyOfRange(payload, 5 ,payload.length - 2);
    }

    public byte[] getTargets() {
        return targets;
    }

    public void logcat() {
        int index = 0;
        double hi_mgdl;
        double hi_mmol;
        double lo_mgdl;
        double lo_mmol;
        int time;

        int items = getByteIU(targets, index++);
        Log.d(TAG, "Targets: Items: " + items);

        for (int i = 0; i < items; i++) {
            hi_mgdl = getShortIU(targets, index + 0x00);
            hi_mmol = getShortIU(targets, index + 0x02) / 10.0;
            lo_mgdl = getShortIU(targets, index + 0x04);
            lo_mmol = getShortIU(targets, index + 0x06) / 10.0;
            time = getByteIU(targets, index + 0x08) * 30;
            Log.d(TAG, "TimePeriod: " + (i + 1) + " hi_mgdl: " + hi_mgdl  + " hi_mmol: " + hi_mmol + " lo_mgdl: " + lo_mgdl  + " lo_mmol: " + lo_mmol + " Time: " + time / 60 + "h" + time % 60 + "m");
            index += 9;
        }
    }
}
