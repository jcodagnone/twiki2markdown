require 'helper'
require 'fileutils'

class TestTwiki2markdown < Test::Unit::TestCase

  context "Output a TWiki file to STDOUT" do
    
  end
  
  
  context "Migrate a TWiki to Gollum" do
    
    setup do
      if File.exists? "test_gollum"
        FileUtils.rm_rf "test_gollum"
      end
      
      @from_path = "test/test_twiki/data/sample_wiki"
      @to_path   = "test/test_gollum"
      
      Twiki2Markdown.do_migration :from => @from_path,
                                  :to   => @to_path,
                                  :verbose => false # Change for debugging
    end
    
    
    should "parse the files from one folder to another" do
      from_files = Dir.open(@from_path).collect
      to_files = Dir.open(@to_path).collect
      
      from_files = from_files.map { |file| file.gsub(/\.txt/,".md").gsub(/WebHome/,"Home") }
      from_files = from_files.select { |file| not (file.match(/Web[A-Z]+[a-z0-9]+/) and not file.match(/WebHome/)) }
      
      from_files.push "images" unless from_files.include? "images"
      to_files.push "images" unless to_files.include? "images"
      
      assert_equal from_files.sort, to_files.sort 
    end
    
    should "copy the images to the /images/ folder" do
      from_files = Dir.open(@from_path).collect
      
      from_files.each do |file| 
        next if file.match(/^\.{1,2}$/)
        
        image_urls = []
        match = nil
        
        f = File.open("#{@from_path}/#{file}")
        f.lines.each do |line|
          image_urls.push match.captures.last  if (match = line.match(/(<img[^<]*)src="%ATTACHURLPATH%(.*)"/))
        end
        f.close
        

        image_urls.each do |image_url|
         assert_equal true, File.exists?("#{@to_path}/images/#{file.gsub(/\.txt/,"").gsub(/WebHome/,"Home")}/#{image_url}")
        end
      end
    end
    
    should "not include any versioning file" do
      Dir.open("#{@to_path}/images/").entries.each do |folder|
        assert_equal true, (Dir.glob("#{@to_path}/images/#{folder}/*.*,v").size == 0)
      end

      assert_equal true, (Dir.glob("#{@to_path}/*.*,v").size == 0)
    end
  end
end
