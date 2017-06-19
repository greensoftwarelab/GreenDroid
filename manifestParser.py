#!/usr/bin/python

import xml.sax, sys

class ManifestHandler( xml.sax.ContentHandler ):
   def __init__(self, path):
      self.path = path
      self.CurrentData = ""
      self.target = ""
      self.package = ""

   # Call when an element starts
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "instrumentation":
         if "android:targetPackage" in attributes:
            self.target = attributes["android:targetPackage"]
      elif tag == "manifest":
         if "package" in attributes:
            self.package = attributes["package"]

  
def main(argv):
   list = []
   for arg in argv:
      path = arg.replace("/AndroidManifest.xml", "")
      # create an XMLReader
      parser = xml.sax.make_parser()
      # turn off namepsaces
      parser.setFeature(xml.sax.handler.feature_namespaces, 0)

      # override the default ContextHandler
      handler = ManifestHandler(path)
      parser.setContentHandler( handler )
      
      parser.parse(arg)

      list.append(handler)
      #print(handler.package)

   p, source, tests, package, testPack="","","","",""
   #count=0
   for h in list:
      if h.target != "":
         #test project founded!!
         tests=h.path
         p=h.target
         list.remove(h)
         #count+=1
         if p != "":
            for x in list:
               isSubstring = (x.package in p) and (x.package != "") and (x.target == "")
               if isSubstring:
                  testPack=h.package
                  source=x.path
                  package=x.package
                  break
      if source != "":
         break
   print(source)
   print(tests)
   print(package)
   print(testPack)

if __name__ == "__main__":
   main(sys.argv[1:])
