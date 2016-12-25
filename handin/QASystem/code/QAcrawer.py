# -*- coding: utf-8 -*- 
import sys
import urllib
import urllib2
import re
from bs4 import BeautifulSoup
import random

#the number of real snippet we get could be smaller than TotalPageNum * 10 slightly little
TotalPageNum = 1
KeywordFile2 = 'key_word_7000'
# KeywordFile = 'key_word2'
# KeywordFile = '样例数据.txt'
KeywordFile = 'question_7000.txt'


urls = ["http://www.baidu.com/s?wd=", "https://zhidao.baidu.com/search?word=", "https://www.google.com/?#q=", "https://www.bing.com/search?q="]

user_agents = ['Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0', \
     'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0', \
     'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533+ \
     (KHTML, like Gecko) Element Browser 5.0', \
     'IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)', \
     'Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)', \
     'Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14', \
     'Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) \
     Version/6.0 Mobile/10A5355d Safari/8536.25', \
     'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) \
     Chrome/28.0.1468.0 Safari/537.36', \
     'Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)']


def craw(question_word, url_num, page_num, file):
	if url_num == 0:
		# url = urls[0] + urllib.quote(question_word.split('\t')[0].decode(sys.stdin.encoding).encode('gbk')) + "&pn="
		url = urls[0] + urllib.quote(question_word.decode(sys.stdin.encoding).encode('utf-8')) + "&pn="
					
		# question_word = question_word.replace('\t', '%09')[:-4]
		url = "%s%d"%(url, page_num * 10)
		
		print url
		request = urllib2.Request(url)
		index = random.randint(0, 9)
		user_agent = user_agents[index]
		request.add_header('User-agent', user_agent)
		
		htmlpage = urllib2.urlopen(request).read()
		
		# print htmlpage
		soup = BeautifulSoup(htmlpage, "lxml")
		# print len(soup.find_all("div", class_="c-abstract"))
		
		snippet = [re.sub(u'<[\d\D]*?>',' ',str(item)) for item in soup.find_all("div", class_="c-abstract")]
		
		# print snippet
		for item in snippet:
			f.writelines(''.join(item.strip().split())+'\n')

		return (len(snippet) <= 4)
	
	elif url_num == 1:
		url = urls[1] + urllib.quote(question_word.decode(sys.stdin.encoding).encode('utf-8')) + "&pn="
		# url = urls[1] + urllib.quote(question_word) + "&pn="
					
		# question_word = question_word.replace('\t', '%09')[:-4]
		url = "%s%d"%(url, page_num * 10)
		
		# print url
		request = urllib2.Request(url)
		index = random.randint(0, 9)
		user_agent = user_agents[index]
		request.add_header('User-agent', user_agent)
		
		htmlpage = urllib2.urlopen(request).read()
		
		# print htmlpage
		soup = BeautifulSoup(htmlpage, "lxml")
		# print len(soup.find_all("div", class_="c-abstract"))
		
		snippet = [re.sub(u'<[\d\D]*?>',' ',str(item)) for item in soup.find_all("dd", class_="dd answer")]
		
		# print snippet
		for item in snippet:
			f.writelines(''.join(item.strip().split())+'\n')

		return (len(snippet) <= 4)


	elif url_num == 2:
		# failed!
		url = urls[2] + urllib.quote(question_word.decode(sys.stdin.encoding).encode('gbk')) + "&start="
					
		# question_word = question_word.replace('\t', '%09')[:-4]
		url = "%s%d"%(url, page_num * 10)
		
		print url
		request = urllib2.Request(url)
		index = random.randint(0, 9)
		user_agent = user_agents[index]
		request.add_header('User-agent', user_agent)
		
		htmlpage = urllib2.urlopen(request).read()
		
		print htmlpage
		soup = BeautifulSoup(htmlpage, "lxml")
		# print len(soup.find_all("div", class_="c-abstract"))
		
		snippet = [re.sub(u'<[\d\D]*?>',' ',str(item)) for item in soup.find_all("dd", class_="dd answer")]
		
		# print snippet
		for item in snippet:
			f.writelines(''.join(item.strip().split())+'\n')
		return (len(snippet) == 0)

	elif url_num == 3:
		#failed in decode 
		url = urls[3] + urllib.quote(question_word.decode(sys.stdin.encoding).encode('gbk')) + "&first="
					
		# question_word = question_word.replace('\t', '%09')[:-4]
		url = "%s%d"%(url, page_num * 10)
		
		print url
		request = urllib2.Request(url)
		index = random.randint(0, 9)
		user_agent = user_agents[index]
		request.add_header('User-agent', user_agent)
		
		htmlpage = urllib2.urlopen(request).read()
		
		# print htmlpage
		soup = BeautifulSoup(htmlpage, "lxml")

		print len(soup.find_all("p"))

		# print soup.find_all("p")

		snippet = [re.sub(u'<[\d\D]*?>',' ',str(item.encode("UTF-8"))) for item in soup.find_all("p")]
		
		print snippet
		# snippet = soup.find_all("p")
	
		# print soup.p.strings
		for item in snippet:
			f.writelines(''.join(item.strip().split())+'\n')	
		return (len(snippet) == 0)
	




file_keyword = open(KeywordFile2)

with open(KeywordFile, 'r') as kf:
	cnt = 0
	for question_word in kf:
		key_word = file_keyword.readline()

		cnt = cnt + 1;

		if(cnt <= 5765):
			continue;

		# print key_word

		print cnt
		print question_word[:-1].split('\t')[0]


		resname = './snippet_zhidao/snippet_for_'
		resname = "%s%d%s"%(resname, cnt, '.txt')
		
				
		with open(resname, 'wa') as f:
			pagenum = 0
			while pagenum < TotalPageNum:
				# craw(0, pagenum, f)
				# craw(key_word ,1 , pagenum, f)
				if(craw(question_word, 1, pagenum, f)):
					if(craw(key_word ,1, pagenum, f)):
						if(craw(key_word ,0, pagenum, f)):
							craw(question_word, 0, pagenum, f)
				# craw(2, pagenum, f)
				# craw(3, pagenum, f)

				pagenum = pagenum + 1
