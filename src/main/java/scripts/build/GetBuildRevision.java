/**
 * Copyright (c) 2016-2017 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package scripts.build;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;


/**
 * Generates a GIT revision number
 * 
 * References:
 * http://stackoverflow.com/questions/11869412/jgit-using-revwalk-to-get-revcommit-returns-nothing
 * http://stackoverflow.com/questions/4777453/looping-over-commits-for-a-file-with-jgit
 * 
 * remote repos:
 * http://stackoverflow.com/questions/12799573/add-remote-via-jgit
 * 
 * 
 *
 */
public class GetBuildRevision {		

	/**
	 * @param args
	 * args[0]=${project_loc} eg /home/user/git/Connectic/Connectic.Master
	 * args[1]= ${git_work_tree} eg /home/user/git/Connectic
	 */
	public static void main(String[] args) {
		
		if(args.length<1 || 0==args[0].length() || !(new File(args[0]).isDirectory())){
			System.err.println("args[0] must be ${git_work_tree} eg /home/user/git/Connectic");
			System.exit(-1);
		}
		System.out.println("${git_work_tree}="+args[0]);
		
		int revisionNumber = getRevision(args[0]);
		
		// send back revisionNumber for stdout for ant script to collect
		System.out.println("revisionNumber="+revisionNumber);
		System.exit(revisionNumber);
	}
	
	public static int getRevision(String git_work_tree){
		String localPath = git_work_tree;
		int revisionNumber=0;
		
		// First get GIT revision number by counting the commits
		
        try {
			Repository localRepo = new FileRepository(localPath + "/.git");
			System.out.println("Reading "+localPath + "/.git"+"\n");
			//System.out.println("Reading "+localPath + "/.git"+", branch="+localRepo.getBranch()+"\n");
			
			// ALTERNATIVE TECHNIQUE
			RevWalk walk = new RevWalk(localRepo);
			//walk.setRetainBody(false); // removes AuthorIdent and FullMessage
			walk.markStart(walk.parseCommit(localRepo.resolve("HEAD")));
			
			for (Iterator<RevCommit> iterator = walk.iterator(); iterator.hasNext();) {
		        //It never cames in this block

		        @SuppressWarnings("unused")
				RevCommit rev = iterator.next();
//		        System.out.println("hash="+rev.getName());
//		        System.out.println("author="+rev.getAuthorIdent());
//		        System.out.println("message="+rev.getFullMessage());
//		        System.out.println();
		        
		        revisionNumber++;
		    }
			walk.dispose();

			// Anothyer way:
//			Git git = new Git(localRepo);
//			Iterable<RevCommit> log = git.log().call();
//			
//			while (log.iterator().hasNext()) {
//
//		        RevCommit rev = log.iterator().next();
//		        System.out.println(rev.hashCode());
//		        System.out.println(rev.getAuthorIdent().getName());
//		        System.out.println(rev.getFullMessage());
//		    }
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} 
//        catch (NoHeadException e) {
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			e.printStackTrace();
//		}
			
        return revisionNumber;

	}

}
