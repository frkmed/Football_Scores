package barqsoft.footballscores.Utils;

import android.view.View;
import android.widget.TextView;

import barqsoft.footballscores.Adapters.SectionsPagerAdapter;

/**
 * Created by paskalstoyanov on 02/02/16.
 */
public class TextAnimator {
    private final SectionsPagerAdapter.myPageAdapter pagerAdapter;
    private final TextView outgoing;
    private final TextView textView;
    private static final float FACTOR = 0.1f;

    private int actualStart;
    private int start;
    private int end;
    private float positionFactor;


    // TODO: switch with textswitcher
    public TextAnimator(SectionsPagerAdapter.myPageAdapter pagerAdapter, TextView textView, TextView outgoing) {
        this.pagerAdapter = pagerAdapter;
        this.textView = textView;
        this.outgoing = outgoing;
    }

    public void start(int startPosition, int finalPosition) {
        actualStart = startPosition;
        start = Math.min(startPosition, finalPosition);
        end = Math.max(startPosition, finalPosition);
        //@DrawableRes int incomingId = pagerAdapter.getImageId(finalPosition);
        String incomingStr = pagerAdapter.getDayMatchCount(finalPosition);
        positionFactor = 1f / (end - start);
        outgoing.setText(textView.getText());
        outgoing.setVisibility(View.VISIBLE);
        outgoing.setAlpha(1f);
        textView.setText(incomingStr);
    }

    public void end(int finalPosition) {
        //@DrawableRes int incomingId = pagerAdapter.getImageId(finalPosition);
        String incomingStr = pagerAdapter.getDayMatchCount(finalPosition);
        textView.setTranslationX(0f);
        if (finalPosition == actualStart) {
            textView.setText(outgoing.getText());
            outgoing.setAlpha(1f);
        } else {
            textView.setText(incomingStr);
            outgoing.setVisibility(View.GONE);
            textView.setAlpha(1f);
        }
    }

    public void forward(int position, float positionOffset) {
        float offset = getOffset(position, positionOffset);
        int width = textView.getWidth();
        outgoing.setTranslationX(-offset * (FACTOR * width));
        textView.setTranslationX((1 - offset) * (FACTOR * width));
        textView.setAlpha(offset);
    }

    public void backwards(int position, float positionOffset) {
        float offset = getOffset(position, positionOffset);
        int width = textView.getWidth();
        outgoing.setTranslationX((1 - offset) * (FACTOR * width));
        textView.setTranslationX(-(offset) * (FACTOR * width));
        textView.setAlpha(1 - offset);
    }

    private float getOffset(int position, float positionOffset) {
        int positions = position - start;
        return Math.abs(positions * positionFactor + positionOffset * positionFactor);
    }

    public boolean isWithin(int position) {
        return position >= start && position < end;
    }
}
