#!/usr/bin/env ruby
# A script that will pretend to resize a number of images

class Twiki2Markdown
  
  def self.check_params(args)
    puts args.to_json
  end
  
  def self.do_migration(args)
    check_params args
    
    if (not args[0] or not args[1])
      puts "Params required: migrator.rb from-path to-path #{args[0]} #{args[1]}"
    else

      added_files = []

      start_dir = args[0]
      last_dir = args[0].split(/\//)
      to_dir = args[1]
      image_dir = "#{args[0]}/../../pub/#{last_dir[last_dir.size - 1]}"

      `mkdir #{to_dir}`
      `mkdir #{to_dir}/images`

      dir = Dir.open(start_dir)
      dir.each do |file|
        added_files.push file if file.match(/^.*\.txt$/)
      end

      added_files.each do |file|
        if file.match(/Web[A-Z]+[a-z0-9]+/) and not file.match(/WebHome/)
        next
        end

        if not File.exists? to_dir
          Dir.mkdir to_dir
        end

        destination = File.new(to_dir + "/" + file.gsub(/\.txt/,".md"), "w+")

        puts "#{start_dir}/#{file} >>> #{destination.path}"

        simple_name = file.gsub(/\.txt/,"")

        if File.exists? "#{image_dir}/#{simple_name}"
          `mkdir #{to_dir}/images/#{simple_name}`
          `cp #{image_dir}/#{simple_name}/* #{to_dir}/images/#{simple_name}/`
        end
        `java -jar twiki2markdown.jar #{start_dir}/#{file} | iconv -f iso-8859-1 -t utf8 > #{destination.path}`
      end
      `mv #{to_dir}/WebHome.md #{to_dir}/Home.md`
    end
  end
end

