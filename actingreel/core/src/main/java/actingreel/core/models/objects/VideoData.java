package actingreel.core.models.objects;

public class VideoData
{
  private String videohtml;
  private String videoid;
  private String videotitle;
  private String video_author_name;
  private String video_description;
   
  public String getVideoid()
  {
    return this.videoid;
  }
   
  public void setVideoid(String videoid)
  {
    this.videoid = videoid;
  }
   
  public String getVideotitle()
  {
    return this.videotitle;
  }
   
  public void setVideotitle(String videotitle)
  {
    this.videotitle = videotitle;
  }
   
  public String getVideo_author_name()
  {
    return this.video_author_name;
  }
   
  public void setVideo_author_name(String video_author_name)
  {
    this.video_author_name = video_author_name;
  }
   
  public String getVideohtml()
  {
    return this.videohtml;
  }
   
  public void setVideohtml(String videohtml)
  {
    this.videohtml = videohtml;
  }
  
  public String getVideo_description() 
  {
	  return this.video_description;
  }
  
  public void setVideo_description(String video_description)
  {
	 this.video_description = video_description;
  }
}