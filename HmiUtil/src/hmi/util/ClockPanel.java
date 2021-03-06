/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

package hmi.util;


import javax.swing.*;


import java.awt.*;

/**
 * A panel showing the count of a Clock.
 * 
 * @author Dennis Reidsma
 */
public class ClockPanel extends JPanel implements ClockListener
{
   
  private SystemClock clock;
  private JLabel timeLabel;
  double prevTime = -500;
  public ClockPanel(SystemClock c)
  {
    clock = c;
    initUI();
    clock.addClockListener(this);
  
  }
  protected void initUI()
  {
    //set font to something larger?
    timeLabel = new JLabel("00:00");
    //initTime(5.645312d);
    timeLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
    add(timeLabel);
  }
  public void setLabelFont(Font f)
  {
    timeLabel.setFont(f);
  }
  /**
  * initTime() is called before the Clock starts running, and sends some initial time value.
  * This will often equal the time send for the first regular time() call.
  * This is done on the same clock  Thread that is going to send the regular time() calls. 
  */
  public void initTime(double initTime)
  {
    if (initTime - prevTime > 0.010d) 
    {
      timeLabel.setText(String.format("%10.3f", initTime));
      timeLabel.repaint();
      prevTime = initTime;
    }
  }
  
  
  /**
   * time() is called, with the &quot;current time&quot; specified in seconds.
   */
  public void time(double currentTime)
  {
    if (currentTime- prevTime > 0.010d) 
    {
      timeLabel.setText(String.format("%10.3f", currentTime));
      timeLabel.repaint();
      prevTime = currentTime;
    }
  }
}
