package com.deletechecker;

import java.awt.Desktop;
import java.io.File;

public class FileActionService {

    /**
     * 파일을 운영체제의 휴지통으로 안전하게 이동시킵니다.
     * 성공하면 true, 실패하면 false를 반환합니다.
     */
    public boolean trashFile(ReviewItem item) {
        File file = item.getFile();
        
        // 현재 OS가 Desktop API와 휴지통 이동 기능을 지원하는지 확인
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            return Desktop.getDesktop().moveToTrash(file);
        } else {
            // 휴지통을 지원하지 않는 환경(일부 Linux 등)에 대한 예외 처리
            System.err.println("이 OS에서는 휴지통 이동을 지원하지 않습니다. 영구 삭제를 시도합니다: " + file.getName());
            return file.delete(); 
        }
    }

    /**
     * 파일을 유지(Keep)하는 경우 OS 차원에서 할 일은 없지만,
     * 이렇게 메서드를 만들어두면 나중에 로그를 남기거나 UI 코드를 작성할 때 훨씬 깔끔해집니다.
     */
    public void keepFile(ReviewItem item) {
        // 필요하다면 나중에 여기에 로그 기록 기능을 추가할 수 있습니다.
        System.out.println("유지됨: " + item.getFileName());
    }
}
