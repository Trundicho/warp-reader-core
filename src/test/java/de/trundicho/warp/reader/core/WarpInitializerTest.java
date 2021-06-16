package de.trundicho.warp.reader.core;

import de.trundicho.warp.reader.core.controller.WarpInitializer;
import de.trundicho.warp.reader.core.controller.WarpUpdater;
import de.trundicho.warp.reader.core.model.playmode.PlayModeModel;
import de.trundicho.warp.reader.core.model.playmode.PlayState;
import de.trundicho.warp.reader.core.model.playmode.impl.PlayModeModelImpl;
import de.trundicho.warp.reader.core.model.playmode.impl.PlayModel;
import de.trundicho.warp.reader.core.model.speed.DelayModel;
import de.trundicho.warp.reader.core.model.speed.SpeedWeightModel;
import de.trundicho.warp.reader.core.model.speed.WpmSpeedExchanger;
import de.trundicho.warp.reader.core.model.speed.impl.DelayModelImpl;
import de.trundicho.warp.reader.core.model.speed.impl.SpeedWeightModelImpl;
import de.trundicho.warp.reader.core.model.speed.impl.WpmSpeedExchangerImpl;
import de.trundicho.warp.reader.core.model.warpword.TextSplitter;
import de.trundicho.warp.reader.core.model.warpword.impl.WordLengthModelImpl;
import de.trundicho.warp.reader.core.view.api.timer.WarpTimer;
import de.trundicho.warp.reader.core.view.api.widgets.NumberLabelWidget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class WarpInitializerTest {
    private static final int DEFAULT_NUMBER_OF_CHARS_TO_DISPLAY = 15;
    private static final int UPDATES_PER_MINUTE = 250;
    private WarpInitializer warpInitializer;
    private WarpTextWidgetForTest warpTextLabelUpdater;
    private static final int TEST_WARP_DURATION_IN_MS = 3500;
    private PlayModel playModel;
    private PlayModeModel  playModeModel;
    private static final String INITIAL_TEXT = "WarpReader Copy and paste your text or URL into the text field and start reading. As you get faster just increase the tempo as much as you like. Have fun reading at warp speed with WarpReader";

    @BeforeEach
    void setUp() {
        this.playModel = new PlayModel();
        WpmSpeedExchanger wpmSpeedExchanger = new WpmSpeedExchangerImpl();
        double DELAY = wpmSpeedExchanger.exchangeToSpeed(UPDATES_PER_MINUTE);
        DelayModel speedModel = new DelayModelImpl(DELAY);
        playModeModel = new PlayModeModelImpl(PlayState.PAUSE);
        SpeedWeightModel speedWeightModel = new SpeedWeightModelImpl();
        TextSplitter textSplitter = new TextSplitter(new WordLengthModelImpl(DEFAULT_NUMBER_OF_CHARS_TO_DISPLAY));
        warpTextLabelUpdater = new WarpTextWidgetForTest();
        NumberLabelWidget durationWidget = new DurationWidgetForTest();
        WarpTimer warpTimer = new WarpTimerForTest(new WarpUpdater(playModel));
        warpInitializer = new WarpInitializer(warpTextLabelUpdater, speedModel, playModeModel,
                speedWeightModel, textSplitter, playModel, durationWidget, warpTimer);
    }

    @Test
    void test_initialization_of_WarpReader_expect_warped_default_text_in_a_list() throws InterruptedException {
        warpInitializer.initAndStartWarping(INITIAL_TEXT);
        final List<String> warpedTextLines = warpTextLabelUpdater.getWarpedTextLines();
        Thread.currentThread().sleep(TEST_WARP_DURATION_IN_MS);
        checkExpectationThatDefaultTextHasBeenWarped(warpedTextLines);
    }

    @Test
    void test_position_update_of_WarpReader_expect_center_word() throws InterruptedException {
        warpInitializer.initAndStartWarping(INITIAL_TEXT);
        final List<String> warpedTextLines = warpTextLabelUpdater.getWarpedTextLines();
        Thread.currentThread().sleep(TEST_WARP_DURATION_IN_MS);
        playModeModel.setPlayState(PlayState.PAUSE);
        playModel.setCurrentPosition(50);

        final String centerWords = "increase the";
        Assertions.assertEquals(centerWords, warpedTextLines.get(warpedTextLines.size() - 1));
    }

    @Test
    void test_after_initialization_of_WarpReader_expect_warped_input_text_in_a_list() throws InterruptedException {
        warpInitializer.initAndStartWarping(INITIAL_TEXT);
        final List<String> warpedTextLines = warpTextLabelUpdater.getWarpedTextLines();
        Thread.currentThread().sleep(TEST_WARP_DURATION_IN_MS);
        checkExpectationThatDefaultTextHasBeenWarped(warpedTextLines);
        String secondText = "This is a test text. It should be warped after the initialization.";
        warpInitializer.initAndStartWarping(secondText);
        Thread.currentThread().sleep(TEST_WARP_DURATION_IN_MS);
        int i = 14;
        Assertions.assertEquals("This is a test", warpedTextLines.get(i++));
        Assertions.assertEquals("text. It should", warpedTextLines.get(i++));
        Assertions.assertEquals("be warped after", warpedTextLines.get(i++));
        Assertions.assertEquals("the", warpedTextLines.get(i++));
        Assertions.assertEquals("initialization.", warpedTextLines.get(i++));
    }

    private void checkExpectationThatDefaultTextHasBeenWarped(List<String> warpedTextLines) {
        int i = 0;
        Assertions.assertEquals(14, warpedTextLines.size());
        Assertions.assertEquals("WarpReader Copy", warpedTextLines.get(i++));
        Assertions.assertEquals("and paste your", warpedTextLines.get(i++));
        Assertions.assertEquals("text or URL", warpedTextLines.get(i++));
        Assertions.assertEquals("into the text", warpedTextLines.get(i++));
        Assertions.assertEquals("field and start", warpedTextLines.get(i++));
        Assertions.assertEquals("reading. As you", warpedTextLines.get(i++));
        Assertions.assertEquals("get faster just", warpedTextLines.get(i++));
        Assertions.assertEquals("increase the", warpedTextLines.get(i++));
        Assertions.assertEquals("tempo as much", warpedTextLines.get(i++));
        Assertions.assertEquals("as you like.", warpedTextLines.get(i++));
        Assertions.assertEquals("Have fun", warpedTextLines.get(i++));
        Assertions.assertEquals("reading at warp", warpedTextLines.get(i++));
        Assertions.assertEquals("speed with", warpedTextLines.get(i++));
        Assertions.assertEquals("WarpReader", warpedTextLines.get(i++));
    }

}