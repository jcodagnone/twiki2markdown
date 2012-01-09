#!/usr/bin/env ruby
#
# Copyright (c) 2012 Zauber S.A. <http://www.zaubersoftware.com/>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# A script that will pretend to resize a number of images
require 'fileutils'
require 'iconv'

class Twiki2Markdown
  def self.do_migration(args)

    location = File.expand_path "#{File.dirname(__FILE__)}/../bin/"

    if (not args[:from] or not args[:to])
      puts "twiki2markdown --help for usage info"
    else
      args[:branch]  ||= "master"
      args[:remote]  ||= "origin"
      args[:logfile] ||= "/dev/null"
      args[:commit]  ||= "migrating with twiki2markdown"
      
      added_files = []

      start_dir = args[:from]
      last_dir  = args[:from].split(/\//).last
      to_dir    = args[:to]
      image_dir = File.expand_path "#{args[:from]}/../../pub/#{last_dir}"

      FileUtils.mkdir to_dir             unless File.exists? to_dir
      FileUtils.mkdir "#{to_dir}/images" unless File.exists? "#{to_dir}/images"

      dir = Dir.open(start_dir)
      dir.each do |file|
        added_files.push file if file.match(/^.*\.txt$/)
      end

      if not File.exists? to_dir
        Dir.mkdir to_dir
      end

      added_files = Iconv.open("utf8","iso8859-1") { |cd|
        added_files.collect { |s| cd.iconv(s) }
      }

      added_files.each do |file|

        if file.match(/Web[A-Z]+[a-z0-9]+/) and not file.match(/WebHome/)
        next
        end
        
        if (file.match(/(á|ñ|í|ó|ú|é)/))
          new_file = file.gsub /(á|ñ|í|ó|ú|é)/, ""
          FileUtils.mv "#{start_dir}/#{file}", "#{start_dir}/#{new_file}"
          file = new_file  
        end
        
        destination = File.new(to_dir + "/" + file.gsub(/\.txt/,".md"), "w+")

        if args[:verbose]
          puts "Parsing #{file}"
        else
          print "."
          STDOUT.flush
        end

        simple_name = file.gsub(/\.txt/,"")

        if File.exists? "#{image_dir}/#{simple_name}"
          unless File.exists? "#{to_dir}/images/#{simple_name.gsub(/WebHome/, "Home")}"
            FileUtils.mkdir "#{to_dir}/images/#{simple_name.gsub(/WebHome/, "Home")}"
          end
          Dir.open("#{image_dir}/#{simple_name}/").entries.each do |entry|
            next if entry.match(/^\.{1,2}$/)
            unless entry.match /\,v/
              FileUtils.cp("#{image_dir}/#{simple_name}/#{entry}", "#{to_dir}/images/#{simple_name.gsub(/WebHome/, "Home")}")
            end
          end
        end

        `java -jar #{location}/twiki2markdown.jar #{start_dir}/#{file} | iconv -f iso-8859-1 -t utf8 > #{destination.path}`
      end

      FileUtils.mv "#{to_dir}/WebHome.md", "#{to_dir}/Home.md"
      if File.exists? "#{to_dir}/images/WebHome"
        FileUtils.mv "#{to_dir}/images/WebHome","#{to_dir}/images/Home"
      end

      old_pwd = Dir.pwd

      Dir.chdir args[:to]

      if args[:repo]
        `git init > #{ args[:logfile] }`
        `git remote add #{ args[:remote] } #{ args[:repo] } > #{ args[:logfile] }`
        `git pull #{args[:remote]} #{args[:branch]}`
      end

      if File.exists? ".git"
        puts "Adding files to git and commiting..." if args[:verbose]
        `git add . > #{ args[:logfile] }`
        `git commit -m "#{ args[:commit] }"`
      end

      if args[:push] == true
        puts "Pushing..." if args[:verbose]
        `git push #{args[:remote]} #{args[:branch]} > #{ args[:logfile] }`
      end

      Dir.chdir old_pwd
    end
  end
end