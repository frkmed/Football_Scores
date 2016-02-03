package barqsoft.footballscores.Utils;

import android.animation.Animator;
import android.animation.TimeAnimator;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import barqsoft.footballscores.Adapters.SectionsPagerAdapter;

/**
 * Created by paskalstoyanov on 02/02/16.
 * A modified version of https://blog.stylingandroid.com/appbar-part-2/
 */

public class PageChangeListener implements ViewPager.OnPageChangeListener {

    private final TextAnimator textAnimator;

    private int currentPosition = 0;
    private int finalPosition = 0;

    private boolean isScrolling = false;

    public static PageChangeListener newInstance(SectionsPagerAdapter.myPageAdapter pagerAdapter, TextView textView, TextView outgoing) {
        TextAnimator textAnimator = new TextAnimator(pagerAdapter, textView, outgoing);
        return new PageChangeListener(textAnimator);
    }

    PageChangeListener(TextAnimator textAnimator) {
        this.textAnimator = textAnimator;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(isFinishedScrolling(position, positionOffset)) {
            finishScroll(position);
        }
        if (isStartingScrollToPrevious(position, positionOffset)) {
            startScroll(position);
        } else if (isStartingScrollToNext(position, positionOffset)) {
            startScroll(position + 1);
        }
        if (isScrollingToNext(position, positionOffset)) {
            textAnimator.forward(position, positionOffset);
        } else if (isScrollingToPrevious(position, positionOffset)) {
            textAnimator.backwards(position, positionOffset);
        }
    }

    public boolean isFinishedScrolling(int position, float positionOffset) {
        return isScrolling && (positionOffset == 0f && position == finalPosition) || !textAnimator.isWithin(position);
    }

    private boolean isScrollingToNext(int position, float positionOffset) {
        return isScrolling && position >= currentPosition && positionOffset != 0f;
    }

    private boolean isScrollingToPrevious(int position, float positionOffset) {
        return isScrolling && position != currentPosition && positionOffset != 0f;
    }

    private boolean isStartingScrollToNext(int position, float positionOffset) {
        return !isScrolling && position == currentPosition && positionOffset != 0f;
    }

    private boolean isStartingScrollToPrevious(int position, float positionOffset) {
        return !isScrolling && position != currentPosition && positionOffset != 0f;
    }

    private void startScroll(int position) {
        isScrolling = true;
        finalPosition = position;
        textAnimator.start(currentPosition, position);
    }

    private void finishScroll(int position) {
        if (isScrolling) {
            currentPosition = position;
            isScrolling = false;
            textAnimator.end(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //NO-OP
    }

    @Override
    public void onPageSelected(int position) {
        if (!isScrolling) {
            isScrolling = true;
            finalPosition = position;
            textAnimator.start(currentPosition, position);
        }
    }
}

