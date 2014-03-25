package org.smg.gwt.emulator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.kiouri.sliderbar.client.view.SliderBarHorizontal;

public class SliderBar extends SliderBarHorizontal {
    
    ImagesSliderBar images = GWT.create(ImagesSliderBar.class);

    public SliderBar(int maxValue, String width) {
            
            setLessWidget(new Image(images.less()) );
            setMoreWidget(new Image(images.more()));
            setScaleWidget(new Image(images.scalev().getSafeUri()), 16);
            setMoreWidget(new Image(images.more()));
            setDragWidget(new Image(images.drag()));
            this.setWidth(width);
            this.setMaxValue(maxValue);
    }
    
    interface ImagesSliderBar extends ClientBundle{
            
            @Source("images/kdehdrag.png")
            ImageResource drag();

            @Source("images/kdehless.png")
            ImageResource less();

            @Source("images/kdehmore.png")
            ImageResource more();

            @Source("images/kdehscale.png")
            DataResource scalev();
    }
}
