package gaur.himanshu.aidlapp;

parcelable Memo;

interface IMemoService {
    List<Memo> getMemos();
    void deleteMemo(int id);
    void createMemo(String title, String content);
    void updateMemo(int id, String title, String content);
}
